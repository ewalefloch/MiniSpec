package generator;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.type.*;
import metaModel.type.collection.*;
import metaModel.visiteur.Visitor;

import java.util.HashSet;
import java.util.Set;

public class JavaGenerator implements Visitor {

    private final StringBuilder bufferCode = new StringBuilder();

    public final StringBuilder bufferAttributes = new StringBuilder();
    private final StringBuilder bufferGetSet = new StringBuilder();
    private final StringBuilder bufferImport = new StringBuilder();

    private final Set<String> addedImports = new HashSet<>();

    public String getCode() {
        return bufferCode.toString();
    }

    @Override
    public void visitModel(Model e) {
        for (Entity entity : e.getEntities()) {
            entity.accept(this);
        }
    }

    @Override
    public void visitEntity(Entity e) {
        bufferAttributes.setLength(0);
        bufferGetSet.setLength(0);
        bufferImport.setLength(0);
        addedImports.clear();

        for (Attribute a : e.getAttributes()) {
            a.accept(this);
        }

        if (!bufferImport.isEmpty()) {
            bufferCode.append(bufferImport).append("\n");
        }

        bufferCode.append("public class ").append(e.getName()).append(" {\n\n");
        bufferCode.append(bufferAttributes);
        bufferCode.append("\n    public ").append(e.getName()).append("() { }\n");
        bufferCode.append(bufferGetSet);
        bufferCode.append("}\n\n");
    }

    @Override
    public void visitAttribute(Attribute e) {
        bufferAttributes.append("    public ");
        if (e.getType() != null) {
            e.getType().accept(this);
        } else {
            bufferAttributes.append("void");
        }
        bufferAttributes.append(" ").append(e.getName()).append(";\n");

        generateGetterSetter(e);
    }

    // --- VISITE DES TYPES ---

    @Override
    public void visitSimpleType(SimpleType t) {
        bufferAttributes.append(t.getName());
    }

    @Override
    public void visitArrayType(ArrayType t) {
        if (t.getBaseType() != null) t.getBaseType().accept(this);
        bufferAttributes.append("[]");
    }

    @Override
    public void visitListType(ListType t) {
        addImport("java.util.List");
        bufferAttributes.append("List<");
        if (t.getElementType() != null) t.getElementType().accept(this);
        bufferAttributes.append(">");
    }

    @Override
    public void visitSetType(SetType t) {
        addImport("java.util.Set");
        bufferAttributes.append("Set<");
        if (t.getElementType() != null) t.getElementType().accept(this);
        bufferAttributes.append(">");
    }

    @Override
    public void visitBagType(BagType t) {
        addImport("java.util.List");
        bufferAttributes.append("List<");
        if (t.getElementType() != null) t.getElementType().accept(this);
        bufferAttributes.append(">");
    }

    // CELLE QUI MANQUAIT
    @Override
    public void visitCollectionType(CollectionType t) {
        addImport("java.util.Collection");
        bufferAttributes.append("Collection<");
        if (t.getElementType() != null) t.getElementType().accept(this);
        bufferAttributes.append(">");
    }

    @Override
    public void visitResolvedReference(ResolvedReference t) {
        bufferAttributes.append(t.getRefEnt().getName());
    }

    @Override
    public void visitUnresolvedReference(UnresolvedReference t) {
        bufferAttributes.append(t.getName());
    }

    // --- OUTILS ---

    private void addImport(String importName) {
        if (!addedImports.contains(importName)) {
            bufferImport.append("import ").append(importName).append(";\n");
            addedImports.add(importName);
        }
    }

    private void generateGetterSetter(Attribute a) {
        JavaGenerator typeGen = new JavaGenerator();
        if (a.getType() != null) {
            a.getType().accept(typeGen);
        }
        String typeStr = typeGen.bufferAttributes.toString();

        String name = a.getName();
        String capName = name.substring(0, 1).toUpperCase() + name.substring(1);

        bufferGetSet.append("\n    public ").append(typeStr).append(" get").append(capName).append("() {\n")
                .append("        return this.").append(name).append(";\n")
                .append("    }\n");

        bufferGetSet.append("\n    public void set").append(capName).append("(").append(typeStr).append(" ").append(name).append(") {\n")
                .append("        this.").append(name).append(" = ").append(name).append(";\n")
                .append("    }\n");
    }
}