package de.lmu.ifi.dbs.data;

import de.lmu.ifi.dbs.linearalgebra.Matrix;

import java.util.BitSet;
import java.util.Random;

/**
 * Provides a BitVector wrapping a BitSet.
 * 
 * @author Arthur Zimek (<a href="mailto:zimek@dbs.ifi.lmu.de">zimek@dbs.ifi.lmu.de</a>)
 */
public class BitVector extends RealVector<Bit>
{
    /**
     * Storing the bits.
     */
    private BitSet bits;
    
    /**
     * Dimensionality of this bit vector.
     */
    private int dimensionality;
    
    /**
     * Provides a new BitVector corresponding to the specified bits and
     * of the specified dimensionality.
     * 
     * @param bits
     * @param dimensionality
     */
    public BitVector(BitSet bits, int dimensionality)
    {
        if(dimensionality < bits.length())
        {
            throw new IllegalArgumentException("Specified dimensionality "+dimensionality+" is to low for specified BitSet of length "+bits.length());
        }
        this.bits = bits;
        this.dimensionality = dimensionality;
    }
    
    /**
     * Provides a new BitVector corresponding to the bits in the given array.
     * 
     * @param bits an array of bits specifiying the bits in this bit vector
     */
    public BitVector(Bit[] bits)
    {
        this.bits = new BitSet(bits.length);
        for(int i = 0; i < bits.length; i++)
        {
            this.bits.set(i, bits[i].bitValue());
        }
        this.dimensionality = bits.length;
    }

    /**
     * @see FeatureVector#newInstance(T[])
     */
    public FeatureVector<Bit> newInstance(Bit[] values)
    {
        return new BitVector(values);
    }

    /**
     * 
     * 
     * @see de.lmu.ifi.dbs.data.FeatureVector#randomInstance()
     */
    public FeatureVector<Bit> randomInstance(Random random)
    {
        Bit[] randomBits = new Bit[getDimensionality()];
        for(int i = 0; i < randomBits.length; i++)
        {
            randomBits[i] = new Bit(random.nextBoolean());
        }
        return new BitVector(randomBits);
    }

    /**
     * Returns the same as @link{BitVector#randomInstance() randomInstance()}.
     * 
     * @see de.lmu.ifi.dbs.data.FeatureVector#randomInstance(T, T)
     */
    public FeatureVector<Bit> randomInstance(Bit min, Bit max, Random random)
    {
        return randomInstance(random);
    }

    /**
     * 
     * @see de.lmu.ifi.dbs.data.FeatureVector#getDimensionality()
     */
    public int getDimensionality()
    {
        return dimensionality;
    }

    /**
     * 
     * @see de.lmu.ifi.dbs.data.FeatureVector#getValue(int)
     */
    public Bit getValue(int dimension)
    {
        if(dimension < 1 || dimension > dimensionality)
        {
            throw new IllegalArgumentException("illegal dimension: "+dimension);
        }
        return new Bit(bits.get(dimension));
    }

    /**
     * 
     * @see de.lmu.ifi.dbs.data.FeatureVector#getValues()
     */
    public Bit[] getValues()
    {
        Bit[] bitArray = new Bit[dimensionality];
        for(int i = 0; i < dimensionality; i++)
        {
            bitArray[i] = new Bit(bits.get(i));
        }
        return bitArray;
    }

    /**
     * 
     * @see de.lmu.ifi.dbs.data.FeatureVector#getVector()
     */
    public Matrix getVector()
    {
        double[] values = new double[dimensionality];
        for(int i = 0; i < dimensionality; i++)
        {
            values[i] = bits.get(i) ? 1 : 0;
        }
        return new Matrix(values,values.length);
    }

    /**
     * Returns a bit vector equal to this bit vector,
     * if k is not 0, a bit vector with all components
     * equal to zero otherwise.
     * 
     * @see de.lmu.ifi.dbs.data.FeatureVector#multiplicate(double)
     */
    public FeatureVector<Bit> multiplicate(double k)
    {
        if(k == 0)
        {
            return nullVector();
        }
        else
        {
            return this.copy();
        }
    }

    /**
     * Returns the inverse of the bit vector.
     * 
     * @see de.lmu.ifi.dbs.data.FeatureVector#negativeVector()
     */
    public FeatureVector<Bit> negativeVector()
    {
        BitSet newBits = (BitSet) bits.clone();
        newBits.flip(0,dimensionality);
        return new BitVector(newBits,dimensionality);
    }

    /**
     * Returns a bit vector of equal dimensionality but containing 0 only.
     * 
     * @see de.lmu.ifi.dbs.data.FeatureVector#nullVector()
     */
    public FeatureVector<Bit> nullVector()
    {
        return new BitVector(new BitSet(), dimensionality);
    }

    /**
     * Returns a bit vector corresponding to an XOR operation
     * on this and the specified bit vector.
     *     
     * @see FeatureVector#plus(FeatureVector)
     */
    public FeatureVector<Bit> plus(FeatureVector<Bit> fv)
    {
        BitVector bv = new BitVector(fv.getValues());
        bv.bits.xor(this.bits);
        return bv;
    }

    /**
     * 
     * 
     * @see de.lmu.ifi.dbs.data.MetricalObject#copy()
     */
    public FeatureVector<Bit> copy()
    {
        BitVector copy = new BitVector(bits,dimensionality);
        copy.setID(getID());
        return copy;
    }

    /**
     * Returns whether the bit at specified index is set.
     * 
     * 
     * @param index the index of the bit to inspect
     * @return true if the bit at index @code{index}
     * is set, false otherwise.
     */
    public boolean isSet(int index)
    {
        return bits.get(index);
    }
    
    /**
     * Returns whether the bits at all of the specified indices are set.
     * 
     * 
     * @param indices the indices to inspect
     * @return true if the bits at all of the specified indices are set,
     * false otherwise
     */
    public boolean areSet(int[] indices)
    {
        boolean set = true;
        for(int i = 0; i < indices.length && set; i++)
        {
            set &= bits.get(i);
        }
        return set;
    }
    
    /**
     * Returns the indices of all set bits.
     * 
     * 
     * @return the indices of all set bits
     */
    public int[] setBits()
    {
        int[] setBits = new int[bits.size()];
        int index = 0;
        for(int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i+1))
        {
            setBits[index++] = i;
        }
        return setBits;
    }
    
    /**
     * Returns whether this BitVector contains all bits
     * that are set to true in the specified BitSet.
     * 
     * 
     * @param bitset the bits to inspect in this BitVector
     * @return true if this BitVector contains all bits
     * that are set to true in the specified BitSet,
     * false otherwise
     */
    public boolean contains(BitSet bitset)
    {
        boolean contains = true;
        for(int i = bitset.nextSetBit(0); i >= 0 && contains; i = bitset.nextSetBit(i+1))
        {
            contains &= bits.get(i);
        }
        return contains;
    }
    
    /**
     * Returns a copy of the bits currently set in this BitVector.
     * 
     * @return a copy of the bits currently set in this BitVector
     */
    public BitSet getBits()
    {
        return (BitSet) bits.clone();
    }
    
    /**
     * Returns a String representation of this BitVector.
     * The representation is suitable to be parsed
     * by @link{de.lmu.ifi.dbs.parser.BitVectorLabelParser BitVectorLabelParser}.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        Bit[] bitArray = getValues();
        StringBuffer representation = new StringBuffer();
        for(Bit bit : bitArray)
        {
            if(representation.length() > 0)
            {
                representation.append(ATTRIBUTE_SEPARATOR);
            }
            representation.append(bit.toString());
        }
        return representation.toString();
    }

    /**
     * 
     * @see MetricalObject#equals(Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof BitVector)
        {
            BitVector bv = (BitVector) obj;
            return this.getDimensionality() == bv.getDimensionality()
             && this.getBits().equals(bv.getBits());

        }
        else
        {
            return false;
        }
    }
    
    
}
