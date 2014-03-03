package caveGame;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;


public class PhysicsDataJsonParser {
	public static Json json;
	public static JsonValue root;
	private static ObjectMap<String, TileShapeData> shapeDataMap = new ObjectMap<String, TileShapeData>();
	public static ObjectMap<String,TileShapeData> parse(String file) {
		JsonReader jsonReader = new JsonReader();		
		root = jsonReader.parse(Gdx.files.internal(file));
				
		String name = "noname";
		ArrayList<Vector2> vertices = new ArrayList<Vector2>();
		
		for (JsonValue entry = root.child().get(0); entry != null; entry = entry.next()) {								
			name = entry.getString("name");			
//			System.out.println(name);
			
			TileShapeData tmpShapeData = new TileShapeData(name);
			
			for (JsonValue v = entry.get("shapes").get(0).get("vertices").get(0); v != null; v = v.next()) {
//				vertices.add(new Vector2(v.get("x").asFloat(), v.get("y").asFloat()));
				tmpShapeData.addVertex(new Vector2(v.get("x").asFloat(), v.get("y").asFloat()));
			}
			
			shapeDataMap.put(name, tmpShapeData);
//			System.out.println(shapeDataMap.get(name).vertices.toString());
			vertices.clear();
		}		 			
		return shapeDataMap;
	}
}
