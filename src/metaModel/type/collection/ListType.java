package metaModel.type.collection;
import metaModel.type.Type;
import metaModel.visiteur.Visitor;

public class ListType extends CollectionType {
    public ListType(Integer min, Integer max, Type baseType) {
        super("List", min, max, baseType);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitListType(this);
    }
}