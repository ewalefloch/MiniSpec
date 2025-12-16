package metaModel.visiteur;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.type.*;
import metaModel.type.collection.*;

public interface Visitor {
	void visitModel(Model e);
	void visitEntity(Entity e);
	void visitAttribute(Attribute e);

	// Types simples (String, Integer...)
	void visitSimpleType(SimpleType t);

	// Collections
	void visitCollectionType(CollectionType t); // <--- AJOUTÉ
	void visitArrayType(ArrayType t);
	void visitListType(ListType t);
	void visitSetType(SetType t);
	void visitBagType(BagType t);

	// Références
	void visitResolvedReference(ResolvedReference t);
	void visitUnresolvedReference(UnresolvedReference t);
}