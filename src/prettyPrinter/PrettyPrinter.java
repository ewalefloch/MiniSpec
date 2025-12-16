package prettyPrinter;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.type.ArrayType;
import metaModel.type.ResolvedReference;
import metaModel.type.SimpleType;
import metaModel.type.UnresolvedReference;
import metaModel.type.collection.BagType;
import metaModel.type.collection.CollectionType;
import metaModel.type.collection.ListType;
import metaModel.type.collection.SetType;
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

	@Override
	public void visitSimpleType(SimpleType t) {

	}

	@Override
	public void visitCollectionType(CollectionType t) {

	}

	@Override
	public void visitArrayType(ArrayType t) {

	}

	@Override
	public void visitListType(ListType t) {

	}

	@Override
	public void visitSetType(SetType t) {

	}

	@Override
	public void visitBagType(BagType t) {

	}

	@Override
	public void visitResolvedReference(ResolvedReference t) {

	}

	@Override
	public void visitUnresolvedReference(UnresolvedReference t) {

	}


}
