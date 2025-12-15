package metaModel.visiteur;


import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;

public interface Visitor {
	
	void visitModel(Model e);
	void visitEntity(Entity e);
	void visitAttribute(Attribute e);

}
