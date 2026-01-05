package metaModel.minispec.type;

import metaModel.minispec.visiteur.Visitor;

public abstract class ReferenceType extends Type {

    public ReferenceType(String name) {
        super(name);
    }

    @Override
    public abstract void accept(Visitor visitor);
}