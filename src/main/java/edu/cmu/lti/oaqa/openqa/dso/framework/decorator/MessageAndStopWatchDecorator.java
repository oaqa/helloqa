package edu.cmu.lti.oaqa.openqa.dso.framework.decorator;

import edu.cmu.lti.oaqa.openqa.dso.framework.IComponent;

/**
 * Decorator that adds message showing and time-measuring capability to a component
 * @author Hideki Shima
 *
 */
public class MessageAndStopWatchDecorator extends MessageDecorator {

  public MessageAndStopWatchDecorator(IComponent component) {
    super(new StopWatchDecorator(component));
  }

}
