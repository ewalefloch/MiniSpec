package metaModel.minispec.modelValidator;

import metaModel.minispec.Attribute;
import metaModel.minispec.Entity;
import metaModel.minispec.Model;

import java.util.HashSet;
import java.util.Set;

public class ModelValidatorAttributeUniqueHeritage implements ModelValidator {

    @Override
    public void validate(Model model) throws Exception {
        for (Entity e : model.getEntities()) {
            checkUniqueAttribute(e);
        }
    }

    private void checkUniqueAttribute(Entity e) throws Exception {
        Set<String> localAttributeNames = new HashSet<>();
        for (Attribute a : e.getAttributes()) {
            localAttributeNames.add(a.getName());
        }

        Entity parent = e.getSuperEntity();

        while (parent != null) {
            for (Attribute parentAttr : parent.getAttributes()) {
                if (localAttributeNames.contains(parentAttr.getName())) {
                    throw new Exception(
                            "Conflit d'héritage détecté dans l'entité '" + e.getName() + "' : " +
                                    "L'attribut '" + parentAttr.getName() + "' est déjà défini dans l'entité parente '" + parent.getName() + "'."
                    );
                }
            }

            parent = parent.getSuperEntity();
        }
    }
}