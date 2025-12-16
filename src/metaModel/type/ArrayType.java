package metaModel.type;

import metaModel.visiteur.Visitor;

public class ArrayType extends Type {
    private int size;
    private Type baseType;

    public ArrayType(int size, Type baseType) {
        super("Array");
        this.size = size;
        this.baseType = baseType;
    }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public Type getBaseType() { return baseType; }
    public void setBaseType(Type baseType) { this.baseType = baseType; }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitArrayType(this);
    }
}