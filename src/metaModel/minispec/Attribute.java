package metaModel.minispec;

import metaModel.minispec.type.Type;
import metaModel.minispec.visiteur.MinispecElement;
import metaModel.minispec.visiteur.Visitor;

public class Attribute implements MinispecElement {
    private String name;
    private Type type;
    private String initialValue;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    @Override
    public void accept(Visitor v) {
        v.visitAttribute(this);
    }

    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }
}