package metaModel.minispec.type.collection;

import metaModel.minispec.type.Type;
import metaModel.minispec.visiteur.Visitor;

public class BagType extends CollectionType {
    public BagType(Integer min, Integer max, Type baseType) {
        super("Bag", min, max, baseType);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitBagType(this);
    }
}
