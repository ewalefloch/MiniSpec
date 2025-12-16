package metaModel.type.collection;

import metaModel.type.Type;
import metaModel.visiteur.Visitor;

public class CollectionType extends Type {
    private Type elementType;

    private Integer min;
    private Integer max;
    private Integer size; // Pour Array

    public CollectionType(String name, Integer min, Integer max, Type elementType) {
        super(name);
        this.min = min;
        this.max = max;
        this.elementType = elementType;
    }

    public CollectionType(String name, Integer size, Type elementType) {
        super(name);
        this.size = size;
        this.elementType = elementType;
    }

    public Type getElementType() { return elementType; }

    public Integer getSize() { return size; }
    public Integer getMin() { return min; }
    public Integer getMax() { return max; }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitCollectionType(this);
    }
}