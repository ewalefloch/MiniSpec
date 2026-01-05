package metaModel.minispec.type;

import metaModel.minispec.visiteur.Visitor;

public class UnresolvedReference extends ReferenceType {

    public UnresolvedReference(String targetName) {
        super(targetName);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitUnresolvedReference(this);
    }
}