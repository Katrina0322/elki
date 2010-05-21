package de.lmu.ifi.dbs.elki.preprocessing;

import java.util.HashMap;
import java.util.List;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.DistanceResultPair;
import de.lmu.ifi.dbs.elki.database.SpatialIndexDatabase;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreFactory;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.index.tree.LeafEntry;
import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialEntry;
import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialIndex;
import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialNode;
import de.lmu.ifi.dbs.elki.logging.progress.FiniteProgress;
import de.lmu.ifi.dbs.elki.math.MeanVariance;
import de.lmu.ifi.dbs.elki.utilities.datastructures.KNNHeap;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;

/**
 * A preprocessor for annotation of the k nearest neighbors (and their
 * distances) to each database object.
 * 
 * Used for example by {@link de.lmu.ifi.dbs.elki.algorithm.outlier.LOF}.
 * 
 * @author Erich Schubert
 * @param <O> the type of database objects the preprocessor can be applied to
 * @param <D> the type of distance the used distance function will return
 * @param <N> the type of spatial nodes in the spatial index
 * @param <E> the type of spatial entries in the spatial index
 */
@Title("Spatial Approximation Materialize kNN Preprocessor")
@Description("Caterializes the (approximate) k nearest neighbors of objects of a database using a spatial approximation.")
public class SpatialApproximationMaterializeKNNPreprocessor<O extends NumberVector<O, ?>, D extends Distance<D>, N extends SpatialNode<N, E>, E extends SpatialEntry> extends MaterializeKNNPreprocessor<O, D> {
  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public SpatialApproximationMaterializeKNNPreprocessor(Parameterization config) {
    super(config);
  }

  /**
   * Annotates the nearest neighbors based on the values of {@link #k} and
   * {@link #distanceFunction} to each database object.
   */
  @Override
  public void run(Database<O> database) {
    distanceFunction.setDatabase(database);

    SpatialIndexDatabase<O, N, E> db = getSpatialDatabase(database);
    SpatialIndex<O, N, E> index = db.getIndex();

    materialized = DataStoreUtil.makeStorage(database.getIDs(), DataStoreFactory.HINT_STATIC, List.class);
    MeanVariance pagesize = new MeanVariance();
    MeanVariance ksize = new MeanVariance();
    if(logger.isVerbose()) {
      logger.verbose("Approximating nearest neighbor lists to database objects");
    }

    List<E> leaves = index.getLeaves();
    FiniteProgress progress = logger.isVerbose() ? new FiniteProgress("Processing leaf nodes.", leaves.size(), logger) : null;
    for(E leaf : leaves) {
      N node = index.getNode(leaf);
      int size = node.getNumEntries();
      pagesize.put(size);
      if(logger.isDebuggingFinest()) {
        logger.debugFinest("NumEntires = " + size);
      }
      // Collect the ids in this node.
      DBID[] ids = new DBID[size];
      for(int i = 0; i < size; i++) {
        ids[i] = ((LeafEntry)node.getEntry(i)).getDBID();
      }
      HashMap<Pair<DBID, DBID>, D> cache = new HashMap<Pair<DBID, DBID>, D>(size * size * 3 / 8);
      for(DBID id : ids) {
        KNNHeap<D> kNN = new KNNHeap<D>(k, distanceFunction.infiniteDistance());
        for(DBID id2 : ids) {
          if(id.compareTo(id2) == 0) {
            kNN.add(new DistanceResultPair<D>(distanceFunction.distance(id, id2), id2));
          }
          else {
            Pair<DBID, DBID> key = new Pair<DBID, DBID>(id, id2);
            D d = cache.remove(key);
            if(d != null) {
              // consume the previous result.
              kNN.add(new DistanceResultPair<D>(d, id2));
            }
            else {
              // compute new and store the previous result.
              d = distanceFunction.distance(id, id2);
              kNN.add(new DistanceResultPair<D>(d, id2));
              // put it into the cache, but with the keys reversed
              key.first = id2;
              key.second = id;
              cache.put(key, d);
            }
          }
        }
        ksize.put(kNN.size());
        materialized.put(id, kNN.toSortedArrayList());
      }
      if(this.debug) {
        if(cache.size() > 0) {
          logger.warning("Cache should be empty after each run, but still has " + cache.size() + " elements.");
        }
      }
      if(progress != null) {
        progress.incrementProcessed(logger);
      }
    }
    if(progress != null) {
      progress.ensureCompleted(logger);
    }
    if(logger.isVerbose()) {
      logger.verbose("Average page size = " + pagesize.getMean() + " +- " + pagesize.getStddev());
      logger.verbose("On average, " + ksize.getMean() + " +- " + ksize.getStddev() + " neighbors returned.");
    }
  }

  /**
   * Do some (limited) type checking, then cast the database into a spatial
   * database.
   * 
   * @param database Database
   * @return Spatial database.
   * @throws IllegalStateException when the cast fails.
   */
  @SuppressWarnings("unchecked")
  private SpatialIndexDatabase<O, N, E> getSpatialDatabase(Database<O> database) throws IllegalStateException {
    if(!(database instanceof SpatialIndexDatabase)) {
      throw new IllegalStateException("Database must be an instance of " + SpatialIndexDatabase.class.getName());
    }
    SpatialIndexDatabase<O, N, E> db = (SpatialIndexDatabase<O, N, E>) database;
    return db;
  }
}