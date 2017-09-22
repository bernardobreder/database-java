package org.breder.database.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Mapa orientado a nós.
 * 
 * 
 * @author Tecgraf
 */
public class NodeHashMap {

  /** Nodes */
  private InternalNode[] nodes;
  /** Quantidade máxima de elementos por node */
  private int maxNumberOfElementPerNode;
  /** Quantidade de elementos no mapa todo */
  private int numberOfElements;
  /** Quantidade de elementos no mapa todo */
  private int numberOfNodes;
  /** Indica se o mapa mudou */
  private boolean changed;
  /** Interface externa */
  private NodeHashMapInterface action;

  /**
   * Construtor padrão
   * 
   * @param action
   * @throws IOException
   */
  public NodeHashMap(NodeHashMapInterface action) throws IOException {
    this(action, 16);
  }

  /**
   * Construtor padrão
   * 
   * @param action
   * @param maxElementPerNode número máximo de elementos por node
   * @throws IOException
   */
  public NodeHashMap(NodeHashMapInterface action, int maxElementPerNode)
    throws IOException {
    this.action = action;
    InputStream rootInput =
      this.action == null ? null : this.action.getRootInputStream();
    if (rootInput == null) {
      this.numberOfNodes = 1;
      this.maxNumberOfElementPerNode = maxElementPerNode;
      this.numberOfElements = 0;
      this.nodes = new InternalNode[] { new InternalNode() };
    }
    else {
      DataInputStream input = new DataInputStream(rootInput);
      try {
        this.numberOfElements = input.readInt();
        this.numberOfNodes = input.readInt();
        this.maxNumberOfElementPerNode = input.readInt();
      }
      finally {
        input.close();
      }
      this.nodes = new InternalNode[numberOfNodes];
    }
  }

  /**
   * Adiciona um registro
   * 
   * @param id
   * @param value
   * @throws IOException
   */
  public void add(int id, Object value) throws IOException {
    int i = indexFor(id);
    InternalNode node = this.nodes[i];
    if (node == null) {
      this.nodes[i] = node = this.load(i);
    }
    for (InternalEntry e = node.first; e != null; e = e.next) {
      if (e.id == id) {
        e.value = value;
        node.changed = true;
        return;
      }
    }
    node.first = new InternalEntry(id, value, node.first);
    node.changed = true;
    node.size++;
    if (node.size > maxNumberOfElementPerNode) {
      this.numberOfNodes *= 2;
      this.resize();
    }
    this.numberOfElements++;
    this.changed = true;
  }

  /**
   * Retorna um objeto baseado no id
   * 
   * @param id
   * @return objeto
   * @throws IOException
   */
  public Object get(int id) throws IOException {
    int i = indexFor(id);
    InternalNode node = this.nodes[i];
    if (node == null) {
      this.nodes[i] = node = this.load(i);
    }
    for (InternalEntry e = node.first; e != null; e = e.next) {
      if (e.id == id) {
        return e.value;
      }
    }
    return null;
  }

  /**
   * Atualiza o objeto baseado no id
   * 
   * @param id
   * @param value
   * @throws IOException
   */
  public void set(int id, Object value) throws IOException {
    int i = indexFor(id);
    InternalNode node = this.nodes[i];
    if (node == null) {
      this.nodes[i] = node = this.load(i);
    }
    for (InternalEntry e = node.first; e != null; e = e.next) {
      if (e.id == id) {
        e.value = value;
        node.changed = true;
        this.changed = true;
        break;
      }
    }
  }

  /**
   * Remove o objeto baseado no id
   * 
   * @param id
   * @throws IOException
   */
  public void remove(int id) throws IOException {
    int i = indexFor(id);
    InternalNode node = this.nodes[i];
    if (node == null) {
      this.nodes[i] = node = this.load(i);
    }
    for (InternalEntry e = node.first, last = null; e != null; last = e, e =
      e.next) {
      if (e.id == id) {
        if (last == null) {
          node.first = e.next;
        }
        else {
          last.next = e.next;
        }
        node.size--;
        node.changed = true;
        this.numberOfElements--;
        this.changed = true;
        break;
      }
    }
    if (this.numberOfNodes > 1
      && this.numberOfElements <= this.numberOfNodes
        * this.maxNumberOfElementPerNode / 4) {
      this.numberOfNodes /= 2;
      this.resize();
    }
  }

  /**
   * Retorna a quantidade de elementos no mapa
   * 
   * @return quantidade de elementos no mapa
   */
  public int size() {
    return this.numberOfElements;
  }

  /**
   * Reconstroi o mapa com um novo tamanho
   * 
   * @throws IOException
   */
  private void resize() throws IOException {
    InternalNode[] newTable = new InternalNode[this.numberOfNodes];
    for (int n = 0; n < this.numberOfNodes; n++) {
      InternalNode newNode = new InternalNode();
      newNode.changed = true;
      newTable[n] = newNode;
    }
    for (int n = 0; n < this.nodes.length; n++) {
      InternalNode node = this.nodes[n];
      if (node == null) {
        this.nodes[n] = node = this.load(n);
      }
      for (InternalEntry e = node.first; e != null; e = e.next) {
        int newIndex = indexFor(e.id);
        InternalNode newNode = newTable[newIndex];
        newNode.first = new InternalEntry(e.id, e.value, newNode.first);
        newNode.size++;
      }
    }
    this.nodes = newTable;
  }

  /**
   * Carrega o mapa
   * 
   * @param index
   * @return node
   * @throws IOException
   */
  private InternalNode load(int index) throws IOException {
    InputStream stream = this.action.getNodeInputStream(index);
    DataInputStream input = new DataInputStream(stream);
    try {
      return this.loadNode(input);
    }
    finally {
      input.close();
    }
  }

  /**
   * Realiza a leitura de um node
   * 
   * @param input
   * @return node
   * @throws IOException
   */
  private InternalNode loadNode(DataInputStream input) throws IOException {
    int size = input.readInt();
    InternalNode node = new InternalNode();
    node.size = size;
    InternalEntry aux = null;
    for (int n = 0; n < size; n++) {
      int id = input.readInt();
      Object value = this.action.read(input);
      if (aux == null) {
        node.first = aux = new InternalEntry(id, value, null);
      }
      else {
        aux = aux.next = new InternalEntry(id, value, null);
      }
    }
    return node;
  }

  /**
   * Realiza o salvamento dos nodes
   * 
   * @throws IOException
   */
  public void save() throws IOException {
    if (this.changed) {
      this.action.startSave();
      try {
        {
          OutputStream stream = this.action.getRootOutputStream();
          DataOutputStream output = new DataOutputStream(stream);
          try {
            output.writeInt(this.numberOfElements);
            output.writeInt(this.numberOfNodes);
            output.writeInt(this.maxNumberOfElementPerNode);
          }
          finally {
            output.close();
          }
        }
        {
          for (int n = 0; n < this.numberOfNodes; n++) {
            InternalNode node = this.nodes[n];
            if (node != null && node.changed) {
              OutputStream stream = this.action.getNodeOutputStream(n);
              DataOutputStream output = new DataOutputStream(stream);
              try {
                output.writeInt(node.size);
                for (InternalEntry e = node.first; e != null; e = e.next) {
                  output.writeInt(e.id);
                  this.action.write(output, e.value);
                }
              }
              finally {
                output.close();
              }
              node.changed = false;
            }
          }
        }
        this.changed = false;
      }
      finally {
        this.action.closeSave();
      }
    }
  }

  /**
   * Busca pelo indice da tabela
   * 
   * @param h
   * @return indice da tabela
   */
  private int indexFor(int h) {
    return h & (this.numberOfNodes - 1);
  }

  /**
   * Classe de Nó
   * 
   * @author Tecgraf
   */
  private static class InternalNode {

    /** Indica se o node mudou */
    public boolean changed;
    /** Primeiro elemento do node */
    public InternalEntry first;
    /** Quantidade de elementos no node */
    public int size;

  }

  /**
   * Classe de Nó
   * 
   * @author Tecgraf
   */
  private static class InternalEntry {

    /** Código da Chave */
    public int id;
    /** Valor */
    public Object value;
    /** Próximo elemento */
    public InternalEntry next;

    /**
     * Construtor
     * 
     * @param id
     * @param value
     * @param next
     */
    public InternalEntry(int id, Object value, InternalEntry next) {
      this.id = id;
      this.value = value;
      this.next = next;
    }

  }

  /**
   * Implementa as operações externa a essa estrutura
   * 
   * @author Tecgraf
   */
  public static interface NodeHashMapInterface {

    /**
     * Escreve o objeto na stream
     * 
     * @param output
     * @param value
     * @throws IOException
     */
    public void write(DataOutputStream output, Object value) throws IOException;

    /**
     * Retorna a stream de escrita para a estrutura
     * 
     * @return stream
     * @throws IOException
     */
    public OutputStream getRootOutputStream() throws IOException;

    /**
     * Retorna a stream de escrita para a estrutura
     * 
     * @return stream
     * @throws IOException
     */
    public InputStream getRootInputStream() throws IOException;

    /**
     * Evento que notifica que finalizou o salvamento.
     * 
     * @throws IOException
     */
    public void closeSave() throws IOException;

    /**
     * Evento que notifica que está começando a salvar
     * 
     * @throws IOException
     */
    public void startSave() throws IOException;

    /**
     * Realiza a leitura de um objeto
     * 
     * @param input
     * @return objeto
     * @throws IOException
     */
    public Object read(DataInputStream input) throws IOException;

    /**
     * Recupera a stream para saida
     * 
     * @param index
     * @return stream
     * @throws IOException
     */
    public OutputStream getNodeOutputStream(int index) throws IOException;

    /**
     * Retorna a stream de um node
     * 
     * @param index
     * @return stream
     * @throws IOException
     */
    public InputStream getNodeInputStream(int index) throws IOException;

    /**
     * Representa um node
     * 
     * @author Tecgraf
     */
    public static class Node {

      /** Id */
      public final int id;
      /** Valor */
      public final Object value;

      /**
       * Construtor
       * 
       * @param id
       * @param value
       */
      public Node(int id, Object value) {
        super();
        this.id = id;
        this.value = value;
      }

    }

  }

}
