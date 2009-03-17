package experimentalcode.erich.visualization.svg;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import experimentalcode.erich.scales.LinearScale;

public class SVGAxis {

  /**
   * Flag for axis label position. First char: right-hand or left-hand side of
   * line. Second char: text alignment
   * 
   */
  private enum ALIGNMENT {
    LL, RL, LC, RC, LR, RR
  }

  /**
   * Plot an axis with appropriate scales
   * 
   * @param parent Containing element
   * @param scale axis scale information
   * @param x1 starting coordinate
   * @param y1 starting coordinate
   * @param x2 ending coordinate
   * @param y2 ending coordinate
   * @param labels control whether labels are printed.
   * @param righthanded control whether to print labels on the right hand side
   *        or left hand side
   */
  public static void drawAxis(SVGDocument document, Element parent, LinearScale scale, double x1, double y1, double x2, double y2, boolean labels, boolean righthanded) {
    assert (parent != null);
    Element line = SVGUtil.svgElement(document, "line");
    SVGUtil.setAtt(line, "x1", x1);
    SVGUtil.setAtt(line, "y1", -y1);
    SVGUtil.setAtt(line, "x2", x2);
    SVGUtil.setAtt(line, "y2", -y2);
    SVGUtil.setAtt(line, "style", "stroke:silver; stroke-width:0.2%;");
    parent.appendChild(line);
  
    double tx = x2 - x1;
    double ty = y2 - y1;
    // ticks are orthogonal
    double tw = ty * 0.01;
    double th = tx * 0.01;
  
    // choose where to print labels.
    ALIGNMENT pos = ALIGNMENT.LL;
    if(labels) {
      double angle = Math.atan2(-ty, tx);
      // System.err.println(tx + " " + (-ty) + " " + angle);
      if(angle < -2.6) { // -pi .. -2.6 = -180 .. -150
        pos = righthanded ? ALIGNMENT.RC : ALIGNMENT.LC;
      }
      else if(angle < -0.5) { // -2.3 .. -0.7 = -130 .. -40
        pos = righthanded ? ALIGNMENT.RL : ALIGNMENT.LR;
      }
      else if(angle < 0.5) { // -0.5 .. 0.5 = -30 .. 30
        pos = righthanded ? ALIGNMENT.RC : ALIGNMENT.LC;
      }
      else if(angle < 2.6) { // 0.5 .. 2.6 = 30 .. 150
        pos = righthanded ? ALIGNMENT.RR : ALIGNMENT.LL;
      }
      else { // 2.6 .. pi = 150 .. 180
        pos = righthanded ? ALIGNMENT.RC : ALIGNMENT.LC;
      }
    }
    // vertical text offset; align approximately with middle instead of
    // baseline.
    double textvoff = 0.007;
  
    // draw ticks on x axis
    for(double tick = scale.getMin(); tick <= scale.getMax(); tick += scale.getRes()) {
      Element tickline = SVGUtil.svgElement(document, "line");
      double x = x1 + tx * scale.getScaled(tick);
      double y = y1 + ty * scale.getScaled(tick);
      SVGUtil.setAtt(tickline, "x1", x - tw);
      SVGUtil.setAtt(tickline, "y1", -y - th);
      SVGUtil.setAtt(tickline, "x2", x + tw);
      SVGUtil.setAtt(tickline, "y2", -y + th);
      SVGUtil.setAtt(tickline, "style", "stroke:black; stroke-width:0.1%;");
      parent.appendChild(tickline);
      // draw labels
      if(labels) {
        Element text = SVGUtil.svgElement(document, "text");
        SVGUtil.setAtt(text, "style", "font-size: 0.2%");
        switch(pos){
        case LL:
        case LC:
        case LR:
          SVGUtil.setAtt(text, "x", x - tw * 2);
          SVGUtil.setAtt(text, "y", -y - th * 3 + textvoff);
          break;
        case RL:
        case RC:
        case RR:
          SVGUtil.setAtt(text, "x", x + tw * 2);
          SVGUtil.setAtt(text, "y", -y + th * 3 + textvoff);
        }
        switch(pos){
        case LL:
        case RL:
          SVGUtil.setAtt(text, "text-anchor", "start");
          break;
        case LC:
        case RC:
          SVGUtil.setAtt(text, "text-anchor", "middle");
          break;
        case LR:
        case RR:
          SVGUtil.setAtt(text, "text-anchor", "end");
          break;
        }
        text.setTextContent(scale.formatValue(tick));
        parent.appendChild(text);
      }
    }
  }

}
