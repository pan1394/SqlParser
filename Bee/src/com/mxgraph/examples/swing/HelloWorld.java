package com.mxgraph.examples.swing;

import javax.swing.JFrame;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

public class HelloWorld extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2707712944901661771L;

	public HelloWorld() {
		super("Hello, World!");

		mxGraph graph = new mxGraph();
		mxCell parent = (mxCell) graph.getDefaultParent();

		graph.getModel().beginUpdate();
		try {
			mxCell v1 =(mxCell) graph.getModel().getRoot();
			//v1.setVertex(true);
			//mxGeometry v=new mxGeometry(40,40,50,50);
			
			
			mxCell s=new mxCell();
			//s.setStyle("gradientColor=gray;fillColor=lightgray");
			s.setValue("Source");
			s.setVertex(true);
			mxGeometry sg=new mxGeometry(0,0,350,350);
			sg.setRelative(true);
			sg.setOffset(new mxPoint(20,20));
			s.setGeometry(sg);
			v1.insert(s);
			
			
			mxCell t=new mxCell();
			t.setVertex(true);
			t.setValue("Target");
			mxGeometry tg=new mxGeometry(0,0,20,20);
			tg.setOffset(new mxPoint(400,400));
			tg.setRelative(true);
			t.setGeometry(tg);
			v1.insert(t);
			
			graph.setSelectionCell(t);
			
			mxCell edge=new mxCell();
			edge.setGeometry(new mxGeometry());
			edge.getGeometry().setRelative(true);
			
			edge.setEdge(true);
			edge.setSource(s);
			edge.setTarget(t);
			
			//edge.setParent(v1);
			
			//graph.addCell(v1);
			v1.insert(edge);
			
			
		} finally {
			graph.getModel().endUpdate();

		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
	}

	public static void main(String[] args) {
		HelloWorld frame = new HelloWorld();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 320);
		frame.setVisible(true);
	}

}
