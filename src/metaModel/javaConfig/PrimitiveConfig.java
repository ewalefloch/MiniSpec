package metaModel.javaConfig;

import metaModel.javaConfig.visiteur.JavaConfigElement;
import metaModel.javaConfig.visiteur.VisitorConfig;

public class PrimitiveConfig implements JavaConfigElement {
    private final String name;
    private final String javaType;
    private final String packageName;

    public PrimitiveConfig(String name, String javaType, String packageName) {
        this.name = name;
        this.javaType = javaType;
        this.packageName = packageName;
    }

    public String getName() { return name; }
    public String getJavaType() { return javaType; }
    public String getPackageName() { return packageName; }

    @Override
    public void accept(VisitorConfig v) {
        v.visitPrimitiveConfig(this);
    }
}