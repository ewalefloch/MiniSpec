package prettyPrinter;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.visiteur.Visitor;

public class PrettyPrinter implements Visitor {
	String result = "";
	
	public String result() {
		return result;
	}

	@Override
	public void visitModel(Model e) {
		result = "model ;\n\n";
		
		for (Entity n : e.getEntities()) {
			n.accept(this);
		}
		result = result + "end model\n";
	}

	@Override
	public void visitEntity(Entity e) {
		result = result + "entity " + e.getName() + ";\n";
		for (Attribute a : e.getAttributes()){
			a.accept(this);
		}
		result = result + "\nend entity;\n";
	}

	@Override
	public void visitAttribute(Attribute e) {
		result = result + e.getName() + " : " + e.getType() + ";\n";
	}


}
