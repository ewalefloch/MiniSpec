package prettyPrinter;

import metaModel.minispec.Attribute;
import metaModel.minispec.Entity;
import metaModel.minispec.Model;
import metaModel.minispec.type.ArrayType;
import metaModel.minispec.type.ResolvedReference;
import metaModel.minispec.type.SimpleType;
import metaModel.minispec.type.UnresolvedReference;
import metaModel.minispec.type.collection.BagType;
import metaModel.minispec.type.collection.CollectionType;
import metaModel.minispec.type.collection.ListType;
import metaModel.minispec.type.collection.SetType;
import metaModel.minispec.visiteur.Visitor;

public class PrettyPrinter implements Visitor {

	// On utilise un StringBuilder, c'est plus propre et performant pour la concaténation
	private StringBuilder buffer = new StringBuilder();

	public String result() {
		return buffer.toString();
	}

	@Override
	public void visitModel(Model e) {
		buffer.append("model :");
		for (Entity n : e.getEntities()) {
			n.accept(this);
			buffer.append("\n");
		}
		buffer.append("end model\n");
	}

	@Override
	public void visitEntity(Entity e) {
		buffer.append("entity ").append(e.getName());

		if (e.getSuperEntity() != null) {
			buffer.append(" extends ").append(e.getSuperEntity().getName());
		}

		buffer.append(" {\n");

		for (Attribute a : e.getAttributes()) {
			a.accept(this);
		}

		buffer.append("} end entity;\n");
	}

	@Override
	public void visitAttribute(Attribute e) {
		buffer.append("\t").append(e.getName()).append(" : ");

		if (e.getType() != null) {
			e.getType().accept(this);
		} else {
			buffer.append("void");
		}

		buffer.append(";\n");
	}

	// --- VISITE DES TYPES ---

	@Override
	public void visitSimpleType(SimpleType t) {
		buffer.append(t.getName());
	}

	@Override
	public void visitResolvedReference(ResolvedReference t) {
		buffer.append(t.getRefEnt().getName());
	}

	@Override
	public void visitUnresolvedReference(UnresolvedReference t) {
		buffer.append(t.getName());
	}

	@Override
	public void visitArrayType(ArrayType t) {
		buffer.append("Array<");
		if (t.getBaseType() != null) t.getBaseType().accept(this);
		buffer.append(">");
	}

	@Override
	public void visitListType(ListType t) {
		buffer.append("List<");
		if (t.getElementType() != null) t.getElementType().accept(this);
		buffer.append(">");
	}

	@Override
	public void visitSetType(SetType t) {
		buffer.append("Set<");
		if (t.getElementType() != null) t.getElementType().accept(this);
		buffer.append(">");
	}

	@Override
	public void visitBagType(BagType t) {
		buffer.append("Bag<");
		if (t.getElementType() != null) t.getElementType().accept(this);
		buffer.append(">");
	}

	@Override
	public void visitCollectionType(CollectionType t) {
		// Cas générique (fallback)
		buffer.append("Collection<");
		if (t.getElementType() != null) t.getElementType().accept(this);
		buffer.append(">");
	}
}