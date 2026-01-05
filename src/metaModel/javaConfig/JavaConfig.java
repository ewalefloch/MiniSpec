package metaModel.javaConfig;

import metaModel.javaConfig.visiteur.JavaConfigElement;
import metaModel.javaConfig.visiteur.VisitorConfig;

import java.util.ArrayList;
import java.util.List;

public class JavaConfig implements JavaConfigElement {

    private final List<ModelConfig> models = new ArrayList<>();

    private final List<PrimitiveConfig> primitives = new ArrayList<>();

    public void addModel(ModelConfig m) {
        this.models.add(m);
    }

    public void addPrimitive(PrimitiveConfig p) {
        this.primitives.add(p);
    }

    public List<ModelConfig> getModels() { return models; }
    public List<PrimitiveConfig> getPrimitives() { return primitives; }

    public String getPackageForModel(String modelName) {
        for (ModelConfig m : models) {
            if (m.getName().equals(modelName)) return m.getPackageName();
        }
        return null;
    }

    public PrimitiveConfig getPrimitive(String minispecName) {
        for (PrimitiveConfig p : primitives) {
            if (p.getName().equals(minispecName)) return p;
        }
        return null;
    }

    @Override
    public void accept(VisitorConfig v) {
        v.visitJavaConfig(this);
    }
}