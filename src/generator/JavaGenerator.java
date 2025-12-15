package generator;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.visiteur.Visitor;

public class JavaGenerator implements Visitor {
    private StringBuilder buffer = new StringBuilder();
    private StringBuilder bufferGetSet = new StringBuilder();
    public String getCode() {
        return buffer.toString();
    }

    @Override
    public void visitModel(Model e) {
        for (Entity entity : e.getEntities()) {
            entity.accept(this);
        }
    }

    @Override
    public void visitEntity(Entity e) {
        buffer.append("public class ").append(e.getName()).append(" {\n");

        for (Attribute a : e.getAttributes()) {
            a.accept(this);
        }

        buffer.append("\n    public ").append(e.getName()).append("() { }\n");

        buffer.append(bufferGetSet.toString());
        buffer.append("}\n\n");
    }

    @Override
    public void visitAttribute(Attribute e) {
        buffer.append("    public ")
                .append(e.getType())
                .append(" ")
                .append(e.getName())
                .append(";\n");

        generateGetterSetter(e);
    }

    private void generateGetterSetter(Attribute a) {
        String type = a.getType();
        String name = a.getName();

        String capName = name.substring(0, 1).toUpperCase() + name.substring(1);

        bufferGetSet.append("    public ").append(type).append(" get").append(capName).append("() {\n")
                .append("        return this.").append(name).append(";\n")
                .append("    }\n\n");

        bufferGetSet.append("    public void set").append(capName).append("(").append(type).append(" ").append(name).append(") {\n")
                .append("        this.").append(name).append(" = ").append(name).append(";\n")
                .append("    }\n\n");
    }
}