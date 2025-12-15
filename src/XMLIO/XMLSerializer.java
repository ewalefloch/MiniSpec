package XMLIO;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import metaModel.Attribute;
import metaModel.Entity;
import metaModel.Model;
import metaModel.visiteur.Visitor;

public class XMLSerializer implements Visitor {
	List<Element> elements;
	Element root = null;
	String modelId;
	Integer counter;
	Document doc;

	String currentEntityId = null;

	Document result() {
		return this.doc;
	}

	public XMLSerializer() throws ParserConfigurationException {
		this.elements = new ArrayList<>();
		this.counter = 0;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		this.doc = builder.newDocument();
		root = this.doc.createElement("Root");
		this.doc.appendChild(root);
	}

	private void addIdToElement(Element e) {
		this.counter++;
		Attr attr = this.doc.createAttribute("id");
		attr.setValue("#" + this.counter.toString());
		e.setAttributeNode(attr);
	}

	private void maybeUpdateRootFrom(Element e) {
		String rootId = this.root.getAttribute("model");
		if (rootId.isEmpty()) {
			Attr attr = this.doc.createAttribute("model");
			attr.setValue("#" + this.counter.toString());
			this.root.setAttributeNode(attr);
			modelId = attr.getValue();
		}
	}

	@Override
	public void visitEntity(Entity e) {
		Element elem = this.doc.createElement("Entity");
		this.addIdToElement(elem);

		this.currentEntityId = elem.getAttribute("id");

		Attr attr = doc.createAttribute("model");
		attr.setValue(modelId);
		elem.setAttributeNode(attr);

		Attr attrName = doc.createAttribute("name");
		attrName.setValue(e.getName());
		elem.setAttributeNode(attrName);

		this.root.appendChild(elem);
		elements.add(elem);

		for(Attribute a : e.getAttributes()) {
			a.accept(this);
		}
	}

	@Override
	public void visitAttribute(Attribute a) {
		Element elem = this.doc.createElement("Attribute");
		this.addIdToElement(elem);

		if (this.currentEntityId != null) {
			Attr attrEntity = doc.createAttribute("entity");
			attrEntity.setValue(this.currentEntityId);
			elem.setAttributeNode(attrEntity);
		}

		Attr attrName = doc.createAttribute("name");
		attrName.setValue(a.getName());
		elem.setAttributeNode(attrName);

		Attr attrType = doc.createAttribute("type");
		attrType.setValue(a.getType());
		elem.setAttributeNode(attrType);

		this.root.appendChild(elem);
		elements.add(elem);
	}

	@Override
	public void visitModel(Model e) {
		Element elem = this.doc.createElement("Model");
		this.addIdToElement(elem);
		this.maybeUpdateRootFrom(elem);

		this.root.appendChild(elem);
		elements.add(elem);

		for (Entity n : e.getEntities()) {
			n.accept(this);
		}
	}
}