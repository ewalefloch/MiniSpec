package metaModel.type;

import metaModel.visiteur.Visitor;

public abstract class ReferenceType extends Type {

    public ReferenceType(String name) {
        super(name);
    }

    @Override
    public abstract void accept(Visitor visitor);
}