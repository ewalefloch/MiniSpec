package metaModel.minispec.type;

import metaModel.minispec.visiteur.Visitor;
import metaModel.minispec.Entity;

public class ResolvedReference extends ReferenceType {

    private final Entity refEnt;

    public ResolvedReference(Entity entity) {
        super(entity.getName());
        this.refEnt = entity;
    }

    public Entity getRefEnt() { return refEnt; }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitResolvedReference(this);
    }
}