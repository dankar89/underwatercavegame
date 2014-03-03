package caveGame;

import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class TileShapeData {
	public String name;
	public ArrayList<Vector2> vertices = new ArrayList<Vector2>();

	public TileShapeData(String name) {
		this.name = name;
	}
	
	public TileShapeData(String name, ArrayList<Vector2> vertices) {
		this.name = name;
		this.vertices = vertices;
	}

	public void addVertex(Vector2 v) {		
		this.vertices.add(v);
	}

	@Override
	public String toString() {
		String str = "name: " + this.name + "\n";
		str += "Vertices: \n";
		for (Vector2 v : vertices) {
			str += "\t" + v.toString() + "\n";
		}
		
		return str;
	}
	
//	public void flip(boolean flipX, boolean flipY) {
//		//How should I flip????
//		for (Vector2 v : vertices) {
//			if(flipX) {
//				v.x = 1 - v.x;	
//			}
//			if(flipY) {
//				v.y = 1 - v.y;	
//			}
//		}
//	}
}
