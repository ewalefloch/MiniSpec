package metaModel.minispec;

import metaModel.minispec.visiteur.MinispecElement;
import metaModel.minispec.visiteur.Visitor;

import java.util.ArrayList;
import java.util.List;

public class Entity implements MinispecElement {
	private String name;
	private final List<Attribute> attributes;
	private Entity superEntity;

	public Entity(){
		attributes = new ArrayList<Attribute>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void accept(Visitor v) {
		v.visitEntity(this);
	};

    public List<Attribute> getAttributes() {
        return attributes;
    }

	public void addAttribute(Attribute a) { // Setter helper
		this.attributes.add(a);
	}

	public Entity getSuperEntity(){
		return superEntity;
	}

	public void setSuperEntity(Entity superEntity){
		this.superEntity = superEntity;
	}
}
