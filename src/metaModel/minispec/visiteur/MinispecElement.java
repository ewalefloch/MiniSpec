package metaModel.minispec.visiteur;

public interface MinispecElement {
    void accept(Visitor v);
}
