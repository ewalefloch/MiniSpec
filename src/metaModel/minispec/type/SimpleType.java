package metaModel.minispec.type;

import metaModel.minispec.visiteur.Visitor;

public class SimpleType extends Type {

    public SimpleType(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitSimpleType(this);
    }
}