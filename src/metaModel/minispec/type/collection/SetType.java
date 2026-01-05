package metaModel.minispec.type.collection;

import metaModel.minispec.type.Type;
import metaModel.minispec.visiteur.Visitor;

public class SetType extends CollectionType {
    public SetType(Integer min, Integer max, Type baseType) {
        super("Set", min, max, baseType);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitSetType(this);
    }
}