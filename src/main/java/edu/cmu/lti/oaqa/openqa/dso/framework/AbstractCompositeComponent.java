package edu.cmu.lti.oaqa.openqa.dso.framework;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCompositeComponent implements IComponent {

  public AbstractCompositeComponent( IComponent ... components ) {
    for ( IComponent c : components ) {
      if (c!=null) add(c);
    }
  }
  
  protected List<IComponent> components = new ArrayList<IComponent>();

  @Override
  public void initialize() {
    for ( IComponent c : components ) {
      if (c==null) continue; 
      c.initialize();
    }
  }
  
  public void add( IComponent component ) {
    components.add( component );
  }
}
