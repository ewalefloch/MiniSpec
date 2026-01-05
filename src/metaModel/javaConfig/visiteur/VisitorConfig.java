package metaModel.javaConfig.visiteur;

import metaModel.javaConfig.JavaConfig;
import metaModel.javaConfig.ModelConfig;
import metaModel.javaConfig.PrimitiveConfig;

public interface VisitorConfig {

    void visitModelConfig(ModelConfig modelConfig);

    void visitPrimitiveConfig(PrimitiveConfig primitiveConfig);

    void visitJavaConfig(JavaConfig javaConfig);
}