package generator;

import metaModel.javaConfig.JavaConfig;
import metaModel.javaConfig.PrimitiveConfig;
import metaModel.minispec.Attribute;
import metaModel.minispec.Entity;
import metaModel.minispec.Model;
import metaModel.minispec.type.*;
import metaModel.minispec.type.collection.*;
import metaModel.minispec.visiteur.Visitor;

import java.util.HashSet;
import java.util.Set;

public class JavaGenerator implements Visitor {

    private final JavaConfig config;

    private final StringBuilder bufferCode = new StringBuilder();

    public final StringBuilder bufferAttributes = new StringBuilder();
    private final StringBuilder bufferGetSet = new StringBuilder();
    private final StringBuilder bufferImport = new StringBuilder();
    private final StringBuilder bufferMethods = new StringBuilder();
    private final StringBuilder bufferImplements = new StringBuilder();
    private final StringBuilder bufferInterfaces = new StringBuilder();
    private final Set<String> currentImports = new HashSet<>();
    private final Set<String> currentInterface = new HashSet<>();

    public JavaGenerator(JavaConfig config) {
        this.config = config;
    }

    public JavaGenerator() {
        this.config = null;
    }


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
        bufferImplements.setLength(0);
        bufferInterfaces.setLength(0);
        bufferMethods.setLength(0);

        for (Attribute a : e.getAttributes()) {
            a.accept(this);
        }

        String currentPackage = (config != null) ? config.getPackageForModel(e.getName()) : "";

        if (currentPackage != null && !currentPackage.isEmpty()) {
            bufferCode.append("package ").append(currentPackage).append(";\n\n");
        }

        for (String imp : currentImports) {
            bufferCode.append("import ").append(imp).append(";\n");
        }

        if (!currentImports.isEmpty()) bufferCode.append("\n");

        if (!bufferImport.isEmpty()) {
            bufferCode.append(bufferImport).append("\n");
        }

        bufferCode.append("public class ").append(e.getName());
        if (e.getSuperEntity() != null) {
            bufferCode.append(" extends ").append(e.getSuperEntity().getName());
        }
        if (!bufferImplements.isEmpty()){
            bufferCode.append(" implements ").append(bufferImplements);
        }
        bufferCode.append(" {\n\n");
        bufferCode.append(bufferAttributes);
        bufferCode.append("\n    public ").append(e.getName()).append("() { }\n");
        bufferCode.append(bufferGetSet);

        if (!bufferMethods.isEmpty()) {
            bufferCode.append("\n    // --- Méthodes générées pour les valeurs par défaut ---\n");
            bufferCode.append(bufferMethods);
        }

        bufferCode.append("}\n\n");

        if (!bufferInterfaces.isEmpty()) {
            bufferCode.append(bufferInterfaces);
        }
    }

    @Override
    public void visitAttribute(Attribute e) {
        bufferAttributes.append("    public ");
        if (e.getType() != null) {
            e.getType().accept(this);
        } else {
            bufferAttributes.append("void");
        }
        bufferAttributes.append(" ").append(e.getName());
        if (e.getInitialValue() != null && !e.getInitialValue().isEmpty()) {
            String val = e.getInitialValue();
            bufferAttributes.append(" = ").append(val);
            if (val.contains("(") && val.endsWith(")") && !val.trim().startsWith("new ")) {
                generateMethod(e, val);
            }
        }
        bufferAttributes.append(";\n");

        generateGetterSetter(e);
    }

    // --- VISITE DES TYPES ---

    @Override
    public void visitSimpleType(SimpleType t) {
        String javaType = getConfiguredTypeAndImport(t.getName());
        bufferAttributes.append(javaType);
    }

    @Override
    public void visitArrayType(ArrayType t) {
        String javaType = getConfiguredTypeAndImport("Array");
        bufferAttributes.append(javaType).append("<");
        if (t.getBaseType() != null) t.getBaseType().accept(this);
        bufferAttributes.append(">");
    }

    @Override
    public void visitListType(ListType t) {
        String javaType = getConfiguredTypeAndImport("List");
        bufferAttributes.append(javaType).append("<");
        if (t.getElementType() != null) t.getElementType().accept(this);
        bufferAttributes.append(">");
    }

    @Override
    public void visitSetType(SetType t) {
        String javaType = getConfiguredTypeAndImport("Set");
        bufferAttributes.append(javaType).append("<");
        if (t.getElementType() != null) t.getElementType().accept(this);
        bufferAttributes.append(">");
    }

    @Override
    public void visitBagType(BagType t) {
        String javaType = getConfiguredTypeAndImport("Bag");
        bufferAttributes.append(javaType).append("<");
        if (t.getElementType() != null) t.getElementType().accept(this);
        bufferAttributes.append(">");
    }

    @Override
    public void visitCollectionType(CollectionType t) {
        String javaType = getConfiguredTypeAndImport("List");
        bufferAttributes.append(javaType).append("<");
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

    private String getConfiguredTypeAndImport(String minispecType) {
        if (config == null) return minispecType;

        PrimitiveConfig pc = config.getPrimitive(minispecType);
        if (pc != null) {
            if (pc.getPackageName() != null && !pc.getPackageName().isEmpty()) {
                this.currentImports.add(pc.getPackageName());
            }
            return pc.getJavaType();
        }
        return minispecType;
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

    private void generateMethod(Attribute a, String callStr) {
        String methodName = callStr.substring(0, callStr.indexOf('(')).trim();
        if (currentInterface.contains(methodName)) {
            return;
        }

        currentInterface.add(methodName);

        JavaGenerator typeGen = new JavaGenerator(this.config);
        if (a.getType() != null) {
            a.getType().accept(typeGen);
        }
        String typeStr = typeGen.bufferAttributes.toString();
        generateInterface(typeStr, methodName);
        bufferMethods.append("    @Override\n");
        bufferMethods.append("    public ").append(typeStr).append(" ").append(methodName).append("() {\n");
        bufferMethods.append("        //TODO\n");
        bufferMethods.append("        return null;\n");
        bufferMethods.append("    }\n\n");
    }

    private void generateInterface(String typeStr,String methodName){
        methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        String interfaceName = methodName + "Interface";

        if (bufferImplements.isEmpty()){
            bufferImplements.append(interfaceName);
        } else {
            bufferImplements.append(", ").append(interfaceName);
        }

        bufferInterfaces.append("public interface ").append(interfaceName).append(" {\n");
        bufferInterfaces.append("    ").append(typeStr).append(" ").append(methodName).append("();\n");
        bufferInterfaces.append("}\n\n");
    }
}