package treePackage;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;


public class TreeController implements Initializable {

    //Not fxml fields
    private Node root;
    private int size;
    private TreeMap<Double, Double> coordinates;
    private SequentialTransition sqTrans = new SequentialTransition();
    private int incX;
    private LinkedList<JFXButton> traversalText = new LinkedList<>();
    private TreeMap<Double, Line> lines;

    //  All FXml Fields
    @FXML
    private JFXButton fill;

    @FXML
    private JFXButton insert;

    @FXML
    private JFXButton search;

    @FXML
    private JFXButton traverse;

    @FXML
    private JFXButton delete;

    @FXML
    private JFXTextField inputField;

    @FXML
    private Pane TreeBGPane;

    @FXML
    private JFXTextArea msgText;


    //------------------------------------------------------------------------------

    /**
     * Scope:>> Initialize the app with 15 random nodes already created
     * Steps:>>
     * 1: Generate 15 random numbers and store them in binary search Tree(Only add numbers to BST)
     * 2: Create 15 buttons as nodes appropriately for each number , add them to linked list
     * 3: animate them as Numbers added in BST with edges fade out
     * 4:
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        coordinates = new TreeMap<>();
        lines = new TreeMap<>();
        fill();


    }

    //------------------------------------------------------------------------------

    @FXML
    void delete(ActionEvent event) {
        sqTrans.getChildren().clear();
        TreeBGPane.getChildren().removeAll(traversalText);
        if (!isEmpty()) {
            String elm = inputField.getText();               // get element
            JFXButton element = new JFXButton(elm);          // get its clone
            boolean isleftChild = false;
            setButtonStyle(element);
            if (inputCheck(element.getText())) {               //Check correct input
                Node current = root;
                Node parent = root;
                while (current.compareTo(element) != 0) {         //search element if equals, end
                    parent = current;
                    if (current.compareTo(element) < 0) {
                        isleftChild = false;
                        current = current.rightChild;
                    } else {
                        isleftChild = true;
                        current = current.leftChild;
                    }

                    if (current == null) {           // if element not found
                        msgText.setText("Element not Found");
                        inputField.clear();
                        return;
                    }
                }
                //found current
                if (current.leftChild == null && current.rightChild == null) {
                    if (current == root) {
                        TreeBGPane.getChildren().remove(root.data);
                        root = null;
                    } else if (isleftChild) {
                        //remove line
                        Line line = lines.get(current.data.getTranslateX() + current.data.getTranslateY());
                        TreeBGPane.getChildren().remove(line);
                        TreeBGPane.getChildren().remove(parent.leftChild.data);
                        parent.leftChild = null;
                    } else {
                        //remove line
                        Line line = lines.get(current.data.getTranslateX() + current.data.getTranslateY());
                        TreeBGPane.getChildren().remove(line);
                        TreeBGPane.getChildren().remove(parent.rightChild.data);
                        parent.rightChild = null;
                    }
                } else if (current.leftChild == null) {         //has no left Child
                    if (current == root) {
                        TranslateTransition tTrans = new TranslateTransition(Duration.millis(400), current.rightChild.data);
                        tTrans.setToX(current.data.getTranslateX());
                        tTrans.setToY(current.data.getTranslateY());
                        tTrans.play();
//                        disableButtons(tTrans);
                        TreeBGPane.getChildren().remove(root.data);
                        //remove line
                        Line line = lines.get(current.rightChild.data.getTranslateX() + current.rightChild.data.getTranslateY());
                        TreeBGPane.getChildren().remove(line);
                        root = current.rightChild;
                    } else if (isleftChild) {
                        //you have got to move the objects' right child to the node to be deleted not like above
                        // so that their children come with them connected
                        TranslateTransition tTrans = new TranslateTransition(Duration.millis(400), current.rightChild.data);
                        tTrans.setToX(parent.leftChild.data.getTranslateX());
                        tTrans.setToY(parent.leftChild.data.getTranslateY());
                        tTrans.play();
//                        disableButtons(tTrans);
                        //remove line
                        Line lineChild = lines.get(current.rightChild.data.getTranslateX() + current.rightChild.data.getTranslateY());  //remove this line.
                        TreeBGPane.getChildren().remove(lineChild);
                        //change the bindings
                        Line lineParent = lines.get(current.data.getTranslateX() + current.data.getTranslateY());    // bind this to successor (right child) its endX, Y properties
                        lineParent.endXProperty().bind(current.rightChild.data.translateXProperty().add(4));
                        lineParent.endYProperty().bind(current.rightChild.data.translateYProperty().add(4));

                        TreeBGPane.getChildren().remove(parent.leftChild.data);
                        parent.leftChild = current.rightChild;
                    } else {
                        TranslateTransition tTrans = new TranslateTransition(Duration.millis(400), current.rightChild.data);
                        tTrans.setToX(parent.rightChild.data.getTranslateX());
                        tTrans.setToY(parent.rightChild.data.getTranslateY());
                        tTrans.play();
//                        disableButtons(tTrans);

                        //remove line
                        Line lineChild = lines.get(current.rightChild.data.getTranslateX()  + current.rightChild.data.getTranslateY());  //remove this line.
                        TreeBGPane.getChildren().remove(lineChild);
                        //change the bindings
                        Line lineParent = lines.get(current.data.getTranslateX() + current.data.getTranslateY());    // bind this to successor (right child) its endX, Y properties
                        lineParent.endXProperty().bind(current.rightChild.data.translateXProperty().add(4));
                        lineParent.endYProperty().bind(current.rightChild.data.translateYProperty().add(4));

                        TreeBGPane.getChildren().remove(parent.rightChild.data);
                        parent.rightChild = current.rightChild;
                    }
                } else if (current.rightChild == null) {
                    if (current == root) {
                        TranslateTransition tTrans = new TranslateTransition(Duration.millis(400), current.leftChild.data);
                        tTrans.setToX(current.data.getTranslateX());
                        tTrans.setToY(current.data.getTranslateY());
                        tTrans.play();
//                        disableButtons(tTrans);

                        //remove line
                        Line line = lines.get(current.leftChild.data.getTranslateX() + current.leftChild.data.getTranslateY());
                        TreeBGPane.getChildren().remove(line);
                        TreeBGPane.getChildren().remove(root.data);
                        root = current.leftChild;
                    } else if (isleftChild) {
                        TranslateTransition tTrans = new TranslateTransition(Duration.millis(400), current.leftChild.data);
                        tTrans.setToX(parent.leftChild.data.getTranslateX());
                        tTrans.setToY(parent.leftChild.data.getTranslateY());
                        tTrans.play();
//                        disableButtons(tTrans);
                        //remove line
                        Line lineChild = lines.get(current.leftChild.data.getTranslateX() + current.leftChild.data.getTranslateY());  //remove this line.
                        TreeBGPane.getChildren().remove(lineChild);
                        //change the bindings
                        Line lineParent = lines.get(current.data.getTranslateX() + current.data.getTranslateY());    // bind this to successor (right child) its endX, Y properties
                        lineParent.endXProperty().bind(current.leftChild.data.translateXProperty().add(4));
                        lineParent.endYProperty().bind(current.leftChild.data.translateYProperty().add(4));

                        TreeBGPane.getChildren().remove(parent.leftChild.data);
                        parent.leftChild = current.leftChild;
                    } else {
                        TranslateTransition tTrans = new TranslateTransition(Duration.millis(400), current.leftChild.data);
                        tTrans.setToX(parent.rightChild.data.getTranslateX());
                        tTrans.setToY(parent.rightChild.data.getTranslateY());
                        tTrans.play();
//                        disableButtons(tTrans);

                        //remove line
                        Line lineChild = lines.get(current.leftChild.data.getTranslateX() + current.leftChild.data.getTranslateY());  //remove this line.
                        TreeBGPane.getChildren().remove(lineChild);
                        //change the bindings
                        Line lineParent = lines.get(current.data.getTranslateX() + current.data.getTranslateY());    // bind this to successor (right child) its endX, Y properties
                        lineParent.endXProperty().bind(current.leftChild.data.translateXProperty().add(4));
                        lineParent.endYProperty().bind(current.leftChild.data.translateYProperty().add(4));

                        TreeBGPane.getChildren().remove(parent.rightChild.data);
                        parent.rightChild = current.leftChild;
                    }
                } else {    // when node has both children
                    Node successor = getSuccessor(current);      //REmember to connect new moved nodes' lines start or end bindings.....................

                    if (current == root) {
                        //(move)transit successors' right child to successors' position
                        TranslateTransition tTrans = new TranslateTransition(Duration.millis(400), successor.data);
                        tTrans.setToX(root.data.getTranslateX());
                        tTrans.setToY(root.data.getTranslateY());
                        tTrans.play();
//                        disableButtons(tTrans);

                        //change the bindings
                        Line rightLine = lines.get(root.rightChild.data.getTranslateX() + root.rightChild.data.getTranslateY());    // bind this to successor (right child) its endX, Y properties
                        System.out.println("Root right line: " + rightLine);
                        rightLine.startXProperty().bind(successor.data.translateXProperty().add(23));
                        rightLine.startYProperty().bind(successor.data.translateYProperty().add(17));
                        // the problem is with the lines ....   can't fetch them correctly ............
                        Line leftLine = lines.get(root.leftChild.data.getTranslateX() + root.leftChild.data.getTranslateY());
                        System.out.println("Root left line: " + leftLine);
                        leftLine.startXProperty().bind(successor.data.translateXProperty().add(5));
                        leftLine.startYProperty().bind(successor.data.translateYProperty().add(15));
                        ////////////issue of some line clinging................
                        TreeBGPane.getChildren().remove(root.data);
                        root = successor;
                    } else if (isleftChild) {
                        //(move)transit successors' right child to successors' position
                        TranslateTransition tTrans = new TranslateTransition(Duration.millis(400), successor.data);
                        tTrans.setToX(parent.leftChild.data.getTranslateX());
                        tTrans.setToY(parent.leftChild.data.getTranslateY());
                        tTrans.play();
//                        disableButtons(tTrans);

                        //change the bindings
                        Line rightLine = lines.get(current.rightChild.data.getTranslateX() + current.rightChild.data.getTranslateY());    // bind this to successor (right child) its endX, Y properties
                        System.out.println("leftChild of Parent right line: " + rightLine);
                        rightLine.startXProperty().bind(successor.data.translateXProperty().add(23));
                        rightLine.startYProperty().bind(successor.data.translateYProperty().add(17));

                        Line leftLine = lines.get(current.leftChild.data.getTranslateX() + current.leftChild.data.getTranslateY());         //bind this to left child
                        System.out.println("leftChild of Parent left line: " + leftLine);
                        leftLine.startXProperty().bind(successor.data.translateXProperty().add(5));
                        leftLine.startYProperty().bind(successor.data.translateYProperty().add(15));

                        Line parentLine = lines.get(current.data.getTranslateX() + current.data.getTranslateY());
                        System.out.println("leftChild of Parent, parent line: " + parentLine);
                        parentLine.endXProperty().bind(successor.data.translateXProperty().add(22));
                        parentLine.endYProperty().bind(successor.data.translateYProperty().add(4));

                        TreeBGPane.getChildren().remove(current.data);
                        parent.leftChild = successor;

                    } else {
                        //(move)transit successors' right child to successors' position
                        TranslateTransition tTrans = new TranslateTransition(Duration.millis(400), successor.data);
                        tTrans.setToX(parent.rightChild.data.getTranslateX());
                        tTrans.setToY(parent.rightChild.data.getTranslateY());
                        tTrans.play();
//                        disableButtons(tTrans);

                        //change the bindings
                        Line rightLine = lines.get(current.rightChild.data.getTranslateX() + current.rightChild.data.getTranslateY());    // bind this to successor (right child) its endX, Y properties
                        System.out.println("RightChild of Parent left line: " + rightLine);
                        rightLine.startXProperty().bind(successor.data.translateXProperty().add(23));
                        rightLine.startYProperty().bind(successor.data.translateYProperty().add(17));

                        Line leftLine = lines.get(current.leftChild.data.getTranslateX() + current.leftChild.data.getTranslateY());         //bind this to left child
                        System.out.println("RightChild of Parent left line: " + leftLine);
                        leftLine.startXProperty().bind(successor.data.translateXProperty().add(5));
                        leftLine.startYProperty().bind(successor.data.translateYProperty().add(15));

                        Line parentLine = lines.get(current.data.getTranslateX() + current.data.getTranslateY());
                        System.out.println("Right child of Parent, parent line: " + parentLine);
                        parentLine.endXProperty().bind(successor.data.translateXProperty().add(22));
                        parentLine.endYProperty().bind(successor.data.translateYProperty().add(4));

                        TreeBGPane.getChildren().remove(current.data);
                        parent.rightChild = successor;
                    }

                    successor.leftChild = current.leftChild;
                }
                inputField.clear();
            }
        } else {
            msgText.setText("Tree is Empty");
        }
    }

    private Node getSuccessor(Node node) {
        Node successorParent = node;
        Node current = node.rightChild;
        Node successor = current;
        while (current.leftChild != null) {
            successorParent = current;
            current = current.leftChild;
            successor = current;
        }

        if (!successor.equals(node.rightChild)) {
            // move them with coordinates too
            successorParent.leftChild = successor.rightChild;
            if (successor.rightChild != null) {
                TranslateTransition tTrans = new TranslateTransition(Duration.millis(500), successor.rightChild.data);
                tTrans.setToX(successor.data.getTranslateX());
                tTrans.setToY(successor.data.getTranslateY());
                tTrans.play();
                //connect(bind) start coords of successors' right childs' lines' to its parents'
                //remove line3
                Line lineChild = lines.get(successor.rightChild.data.getTranslateX() + successor.rightChild.data.getTranslateY());  //remove this line.
                TreeBGPane.getChildren().remove(lineChild);
                //change the bindings
                Line lineParent = lines.get(successor.data.getTranslateX() + successor.data.getTranslateY());    // bind this to successor (right child) its endX, Y properties
                System.out.println("Successor right child line: " + lineParent);
                lineParent.endXProperty().bind(successor.rightChild.data.translateXProperty().add(22));
                lineParent.endYProperty().bind(successor.rightChild.data.translateYProperty().add(4));
            }

            successor.rightChild = node.rightChild;
            //also connect lines of successors' right line coords; to node. rightchilds' lines start coords
        }
        if ((successor.equals(node.rightChild)) || (successorParent.leftChild == null)) {
            //remove line
            Line lineChild = lines.get(successor.data.getTranslateX() + successor.data.getTranslateY());
            TreeBGPane.getChildren().remove(lineChild);
        }
        return successor;
    }


    //------------------------------------------------------------------------------

    @FXML
    void fill(ActionEvent event) {
        clear();
        TreeBGPane.getChildren().add(msgText);
        fill();
    }

    private void fill() {       //generates new tree with 15 nodes approx
        Random random = new Random();

        int num;
        for (int i = 0; i < 20; i++) {
            num = random.nextInt(300);
            JFXButton button = new JFXButton(String.valueOf(num));
            TreeBGPane.getChildren().add(button);
            setButtonStyle(button);
            insert(button);
        }
        msgText.setText("");
    }
    //------------------------------------------------------------------------------

    @FXML
    void insert(ActionEvent event) {
        sqTrans.getChildren().clear();
        TreeBGPane.getChildren().removeAll(traversalText);
        String buttonText = inputField.getText().trim();
        if (inputCheck(buttonText)) {
            JFXButton node = new JFXButton(buttonText);
            TreeBGPane.getChildren().add(node);
            setButtonStyle(node);
            insert(node);
        }
    }


    //      ----------------------------------------------------------------------------
    private void insert(JFXButton element) {
        if (element != null) {
            msgText.setText("");
            if (root == null) {
                double mid = TreeBGPane.getPrefWidth() / 2;
                element.setTranslateX(mid);
                element.setTranslateY(15);
                //Create a fading animation:->>>>>>>>>>>>>>>>>>>>>
                FadeTransition fadeInTrans = new FadeTransition(Duration.millis(200), element);
                fadeInTrans.setFromValue(0.0);
                fadeInTrans.setToValue(1.0);
                fadeInTrans.play();
                root = new Node(null, element, null);
                size++;
            } else {
                Node current = root;            // work with end, start line properties, bound with nodes(button) and transit the nodes
                double x;
                double y;
                double elmX;     //elements' coordinates
                double elmY;
                while (true) {
                    if (current.compareTo(element) < 0) {
                        if (current.rightChild == null) {
                            int depth = nodeDepth(current);
                            if (depth > 4) {
                                msgText.setText("Can't Insert Limit Exceeded");
                                element.setVisible(false);
                                return;
                            }
                            // get currents' coordinates
                            //(Animation) create a new line moving towards new nodes' coordinates
                            Line rightLine = new Line();
                            rightLine.setStroke(Color.GREY);
                            rightLine.setStrokeWidth(2);
                            TreeBGPane.getChildren().add(rightLine);
                            x = current.data.getTranslateX();              //the problem is here with translate x and y
                            y = current.data.getTranslateY();
                            rightLine.startXProperty().bind(current.data.translateXProperty().add(23));
                            rightLine.startYProperty().bind(current.data.translateYProperty().add(17));
                            if (current.equals(root)) {
                                x = x + 90;
                                y = y - 25;

                            }
                            element.setTranslateX(x);
                            element.setTranslateY(y);
                            rightLine.endXProperty().bind(element.translateXProperty().add(4));
                            rightLine.endYProperty().bind(element.translateYProperty().add(4));
                            rightLine.setOpacity(0.0);
                            elmX = x + 35;     //elements' new coordinates
                            elmY = y + 60;

                            if (elmX >= TreeBGPane.getPrefWidth()) {
                                elmX = x + 15;
                                elmY = y + 15;
                            }

                            if (coordinates.containsKey(elmX) && coordinates.containsValue(elmY)) {
                                elmX = elmX - 20;
                                elmY = elmY + 25;
                            }
                            coordinates.put(elmX, elmY);

                            //Node Animation
                            FadeTransition fadeInLine = new FadeTransition(Duration.millis(100), rightLine);
                            fadeInLine.setFromValue(0.0);
                            fadeInLine.setToValue(1.0);
                            fadeInLine.play();

                            FadeTransition fadeInTrans = new FadeTransition(Duration.millis(200), element);
                            fadeInTrans.setFromValue(0.0);
                            fadeInTrans.setToValue(1.0);
                            TranslateTransition tTrans = new TranslateTransition(Duration.millis(200), element);
                            tTrans.setToX(elmX);
                            tTrans.setToY(elmY);
                            ParallelTransition prlTrans = new ParallelTransition(fadeInTrans, tTrans);
                            prlTrans.play();
                            element.setTranslateX(elmX);
                            element.setTranslateY(elmY);
                            lines.put(element.getTranslateX() + element.getTranslateY(), rightLine);
                            current.rightChild = new Node(null, element, null);
                            size++;
                            return;
                        }
                        current = current.rightChild;
                    } else if (current.compareTo(element) > 0) {        //no duplicates allowed
                        if (current.leftChild == null) {
                            int depth = nodeDepth(current);
                            if (depth > 4) {
                                msgText.setText("Can't Insert Limit Exceeded");
                                element.setVisible(false);
                                return;
                            }
                            Line leftLine = new Line();
                            leftLine.setStroke(Color.GREY);
                            leftLine.setStrokeWidth(2);
                            TreeBGPane.getChildren().add(leftLine);
                            y = current.data.getTranslateY();
                            x = current.data.getTranslateX();
                            leftLine.startXProperty().bind(current.data.translateXProperty().add(5));
                            leftLine.startYProperty().bind(current.data.translateYProperty().add(15));
                            if (current.equals(root)) {
                                x = x - 90;
                                y = y - 25;
                            }
                            element.setTranslateX(x);
                            element.setTranslateY(y);
                            leftLine.endXProperty().bind(element.translateXProperty().add(22));
                            leftLine.endYProperty().bind(element.translateYProperty().add(5));
                            leftLine.setOpacity(0.0);
                            //store the coordinates in trees
                            elmX = x - 30;     //elements' coordinates
                            elmY = y + 60;

                            if (elmX <= 0) {
                                elmX = x - 15;
                                elmY = y + 15;
                            }
                            //check if there already exists a node thus change the coordinates
                            if (coordinates.containsKey(elmX) && coordinates.containsValue(elmY)) {
                                elmX = elmX + 25;
                                elmY = elmY + 30;
                            }
                            coordinates.put(elmX, elmY);

                            //Animation line
                            FadeTransition fadeInLine = new FadeTransition(Duration.millis(100), leftLine);
                            fadeInLine.setFromValue(0.0);
                            fadeInLine.setToValue(1.0);
                            fadeInLine.play();

                            FadeTransition fadeInTrans = new FadeTransition(Duration.millis(200), element);
                            fadeInTrans.setFromValue(0.0);
                            fadeInTrans.setToValue(1.0);
                            TranslateTransition tTrans = new TranslateTransition(Duration.millis(200), element);
                            tTrans.setToX(elmX);
                            tTrans.setToY(elmY);
                            ParallelTransition prlTrans = new ParallelTransition(fadeInTrans, tTrans);
                            prlTrans.play();

                            element.setTranslateX(elmX);
                            element.setTranslateY(elmY);
                            lines.put((element.getTranslateX() + element.getTranslateY()), leftLine);
                            current.leftChild = new Node(null, element, null);
                            size++;
                            return;
                        }
                        current = current.leftChild;
                    } else {
                        msgText.setText("Duplicate Values are not allowed");
                        element.setVisible(false);
                        return;
                    }
                }

            }
        }


    }

    private void setButtonStyle(JFXButton node) {
        node.setButtonType(JFXButton.ButtonType.RAISED);
        node.prefWidth(35);
        node.prefHeight(35);
        node.setStyle("-fx-background-color: grey; -fx-background-radius: 200;");
        node.setTextFill(Paint.valueOf("WHITE"));   //>>>>>>
        node.setWrapText(true);
    }

    //------------------------------------------------------------------------------

    @FXML
    void search(ActionEvent event) {
        sqTrans.getChildren().clear();
        TreeBGPane.getChildren().removeAll(traversalText);
        if (!isEmpty()) {
            String elm = inputField.getText();               // get element
            JFXButton element = new JFXButton(elm);          // get its clone
            setButtonStyle(element);
            SequentialTransition sqTrans = new SequentialTransition();
            if (inputCheck(element.getText())) {               //Check correct input
                Node current = root;
                while (current.compareTo(element) != 0) {         //search element if equals end
                    ScaleTransition nodeScale = new ScaleTransition(Duration.millis(300));
                    nodeScale.setNode(current.data);
                    nodeScale.setToX(1.5);
                    nodeScale.setToY(1.5);
                    nodeScale.setCycleCount(2);
                    nodeScale.setAutoReverse(true);
                    sqTrans.getChildren().add(nodeScale);

                    if (current.compareTo(element) < 0) {
                        current = current.rightChild;
                    } else {
                        current = current.leftChild;
                    }

                    if (current == null) {           // if element not found
                        sqTrans.stop();
                        msgText.setText("Element " + elm + " not Found");
                        inputField.clear();
                        return;
                    }

                }
                element.setText(current.data.getText());
                element.setTranslateX(current.data.getTranslateX());
                element.setTranslateY(current.data.getTranslateY());
                TreeBGPane.getChildren().add(element);
                ScaleTransition nodeScale = new ScaleTransition(Duration.millis(400), element);
                msgText.setText("Element " + element.getText() + " Found ");
                nodeScale.setToX(1.6);
                nodeScale.setToY(1.6);
                nodeScale.setCycleCount(2);
                nodeScale.setAutoReverse(true);
                sqTrans.getChildren().add(nodeScale);
                TranslateTransition nodeTranslate = new TranslateTransition(Duration.seconds(1), element);
                nodeTranslate.setToX(10);
                nodeTranslate.setToY(10);
                FadeTransition fTrans = new FadeTransition(Duration.seconds(3), element);
                fTrans.setFromValue(1.0);
                fTrans.setToValue(0.0);
                ParallelTransition pTrans = new ParallelTransition(nodeTranslate, fTrans);
                sqTrans.getChildren().add(pTrans);
                sqTrans.play();
                disableButtons(sqTrans);
                inputField.clear();

            }
        } else {
            msgText.setText("Tree is Empty");
        }
    }

    //------------------------------------------------------------------------------


    @FXML
    void traverse(ActionEvent event) {
        sqTrans.getChildren().clear();
        TreeBGPane.getChildren().removeAll(traversalText);
        incX = 3;
        String input = inputField.getText();
        if (input.isEmpty()) {
            msgText.setText("Enter:(1) for 'Pre Order', (2) 'In Order', " +
                    "(3) 'Post Order', (4) 'Level Order Traversal'");
            return;
        } else {
            switch (input) {
                case "1":
                    msgText.setText("\t\t\t\t\t\t\t\t\t\t\tPre Order Traversal: ");
                    preOrderTraversal(root);
                    break;
                case "2":
                    msgText.setText("\t\t\t\t\t\t\t\t\t\t\tIn Order Traversal: ");
                    inOrderTraversal(root);
                    break;
                case "3":
                    msgText.setText("\t\t\t\t\t\t\t\t\t\t\tPost Order Traversal: ");
                    postOrderTraversal(root);
                    break;
                case "4":
                    msgText.setText("\t\t\t\t\t\t\t\t\t\t\tLevel Order Traversal: ");
                    levelOrderTraversal(root);
                    break;
                default:
                    msgText.setText("Wrong input Enter Number :-> 1 - 4");
                    break;
            }
        }
        inputField.clear();
        sqTrans.play();
        disableButtons(sqTrans);
    }


    private void preOrderTraversal(Node node) {
        if (node == null) {
            return;
        }
        JFXButton element = node.data;
        textTraverse(element);
        preOrderTraversal(node.leftChild);
        preOrderTraversal(node.rightChild);

    }

    private void inOrderTraversal(Node node) {
        if (node == null) {
            return;
        }
        inOrderTraversal(node.leftChild);
        JFXButton element = node.data;
        textTraverse(element);
        inOrderTraversal(node.rightChild);
    }

    private void postOrderTraversal(Node node) {
        if (node == null) {
            return;
        }
        postOrderTraversal(node.leftChild);
        postOrderTraversal(node.rightChild);
        JFXButton element = node.data;
        textTraverse(element);
    }

    private Deque<Node> nodes = new ArrayDeque<>();

    private void levelOrderTraversal(Node node) {
        nodes.add(node);
        while (!nodes.isEmpty()) {
            if (node.leftChild != null) {
                nodes.add(node.leftChild);
            }
            if (node.rightChild != null) {
                nodes.add(node.rightChild);
            }
            JFXButton element = nodes.remove().data;
            textTraverse(element);
            node = nodes.peek();
        }

    }

    private void textTraverse(JFXButton element) {
        ScaleTransition nodeScale = new ScaleTransition(Duration.millis(300), element);
        nodeScale.setToX(1.6);
        nodeScale.setToY(1.6);
        nodeScale.setCycleCount(2);
        nodeScale.setAutoReverse(true);
        JFXButton nodeText = new JFXButton(element.getText());
        traversalText.add(nodeText);
        TreeBGPane.getChildren().add(nodeText);
        nodeText.setTranslateX(element.getTranslateX());
        nodeText.setTranslateY(element.getTranslateY());
        TranslateTransition tTrans = new TranslateTransition(Duration.millis(500), nodeText);
        tTrans.setToX(incX);
        tTrans.setToY(400);
        incX += 25;
        sqTrans.getChildren().add(nodeScale);
        sqTrans.getChildren().add(tTrans);
    }


    //private methods.......
//-----------------------------------------------------------------------------
    private int height(Node node) {
        if (node == null) {
            return -1;
        }

        int lDepth = height(node.leftChild);
        int rDepth = height(node.rightChild);

        if (lDepth > rDepth) {
            return lDepth + 1;
        } else {
            return rDepth + 1;
        }
    }

    private boolean inputCheck(String input) {        //Checks input for various values
        if (input.isEmpty()) {
            //prompt for input value then click insert
            msgText.setText("Please Enter Value not exceeding 999");
            return false;
        } else if (input.matches("[\\s!-/:-~]+")) {       //checks if contains other than numbers
            msgText.setText("Please Enter Correct Value not exceeding 999");
            return false;
        } else {
            if (!input.matches("^[0-9]{1,3}")) { //if character limit exceeds 3
                msgText.setText("Character Limit is 3. Enter value between 0 - 999");
                return false;
            }
        }
        return true;
    }

    private void disableButtons(Transition trans) {
        //bind the disable property of buttons with active animation of sqTrans
        fill.disableProperty().bind(trans.statusProperty().isEqualTo(Animation.Status.RUNNING));
        insert.disableProperty().bind(trans.statusProperty().isEqualTo(Animation.Status.RUNNING));
        search.disableProperty().bind(trans.statusProperty().isEqualTo(Animation.Status.RUNNING));
        traverse.disableProperty().bind(trans.statusProperty().isEqualTo(Animation.Status.RUNNING));
        delete.disableProperty().bind(trans.statusProperty().isEqualTo(Animation.Status.RUNNING));
    }

    private int nodeDepth(Node node) {
        //Depth = no: of edges from node to the root node
        Node current = root;
        int count = 0;
        while (!current.equals(node)) {
            count++;
            if (current.compareTo(node.data) < 0) {
                current = current.rightChild;
            } else {
                current = current.leftChild;
            }
        }
        return count;
    }

    private boolean isEmpty() {
        return size == 0;
    }

    private void clear() {
        TreeBGPane.getChildren().clear();
        coordinates.clear();
        root = null;
        size = 0;
    }


    //---------------------------------------------------------------------------------------------------------------------
    private static class Node implements Comparable {
        JFXButton data;
        Node leftChild;
        Node rightChild;

        Node(Node leftChild, JFXButton data, Node rightChild) {
            this.leftChild = leftChild;
            this.data = data;
            this.rightChild = rightChild;
        }


        @Override
        public int compareTo(Object obj) {
            if (this == obj) {
                return 0;
            }
            if (obj != null && obj.getClass().equals(this.data.getClass())) {
                int thisData = Integer.valueOf(this.data.getText());
                int objData = Integer.valueOf(((JFXButton) obj).getText());
                return Integer.compare(thisData, objData);
            }
            return 0;
        }
    }

}
