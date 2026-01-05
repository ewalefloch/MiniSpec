package metaModel.javaConfig;

import metaModel.javaConfig.visiteur.JavaConfigElement;
import metaModel.javaConfig.visiteur.VisitorConfig;

public class ModelConfig implements JavaConfigElement {

   private final String name;
   private final String packageName;

   public ModelConfig(String name, String packageName) {
      this.name = name;
      this.packageName = packageName;
   }

   public String getName() {
      return name;
   }

   public String getPackageName() {
      return packageName;
   }

    @Override
    public void accept(VisitorConfig v) {
        v.visitModelConfig(this);
    }
}
