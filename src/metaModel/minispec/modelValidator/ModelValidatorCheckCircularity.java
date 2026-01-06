package metaModel.minispec.modelValidator;

import metaModel.minispec.Entity;
import metaModel.minispec.Model;

import java.util.HashSet;
import java.util.Set;

public class ModelValidatorCheckCircularity implements ModelValidator {

    @Override
    public void validate(Model model) throws Exception {
        for (Entity e : model.getEntities()) {
            checkCircularity(e);
        }
    }

    private void checkCircularity(Entity e) throws Exception {
        Set<Entity> visited = new HashSet<>();
        Entity current = e;

        while (current != null) {
            if (visited.contains(current)) {
                throw new Exception("Erreur de Circularité détectée sur l'entité : " + e.getName());
            }
            visited.add(current);
            current = current.getSuperEntity();
        }
    }
}
