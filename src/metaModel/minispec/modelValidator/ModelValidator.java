package metaModel.minispec.modelValidator;

import metaModel.minispec.Model;

public interface ModelValidator {
    void validate(Model model) throws Exception;
}
