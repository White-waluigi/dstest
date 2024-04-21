package dstest.rendering;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import scene.Renderable;
import scene.SceneNode;

public class OBJLoader {

	public static OBJLoader singleton = new OBJLoader();

	public class WFObject {
		public float[] vertices;
		public float[] uvs;
		public int[] indices;

		public float[] idColor;
		public WFMtl mtl;

		public String toString() {
			return ("WFObject:\n"+
		    "Vertices: "+Arrays.toString(vertices)+"\n"+
			"UVs: "+Arrays.toString(uvs)+"\n"+
		    "Indices: "+Arrays.toString(indices)+"\n"+
			"ID Color: "+Arrays.toString(idColor)+"\n");
			
		}
	}

	public class WFMtl {
		public String texture;
		
		public WFMtl clone() {
			WFMtl mtl = new WFMtl();
			mtl.texture = texture;
			return mtl;
		}
	}

	public void importMaterialFile(String file,String path, HashMap<String, WFMtl> materials) {

		String curName = null;

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line;

			while (true) {
				line = reader.readLine();
				if (line == null) {
					break;
				}
				// only split on the first space
				String[] parts = line.split(" ", 2);
				if (parts[0].equals("newmtl")) {

					curName = parts[1];
				} else if (parts[0].equals("map_Kd")) {
					WFMtl mtl = new WFMtl();
					mtl.texture = path+parts[1];
					materials.put(curName, mtl);
				}

				
			}
			reader.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public List<Integer> triangulatePolygon(List<Float> inputVertices, List<Float> inputUVs,
			List<Integer> inputIndices) {

		List<Integer> newIndices = new ArrayList<Integer>();

		for (int i = 2; i < inputIndices.size(); i++) {
			int firstIndex = inputIndices.get(0);
			int secondIndex = inputIndices.get(i - 1);
			int thirdIndex = inputIndices.get(i);

			newIndices.add(firstIndex);
			newIndices.add(secondIndex);
			newIndices.add(thirdIndex);

		}

		return newIndices;

	}

	public SceneNode load(String file,String path, SceneNode root) {

		List<Float> rawVertices = new ArrayList<Float>();
		List<Float> rawUVs = new ArrayList<Float>();
		List<Float> vertices = new ArrayList<Float>();
		int rawVertOffset = 0;
		int rawUVOffset = 0;
		int rawIndexOffset = 0;

		String curObjName = null;

		List<Float> uvs = new ArrayList<Float>();
		List<Integer> indices = new ArrayList<Integer>();

		List<WFObject> objects = new ArrayList<WFObject>();

		WFMtl curMtl = null;

		HashMap<String, WFMtl> materials = new HashMap<String, WFMtl>();

		BufferedReader reader = null;
		String line = null;
		int lineNum = 0;
		try {

			
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(path+file)));

			do {
				lineNum ++;
				line = reader.readLine();

				if (lineNum % 100000 == 0)
					System.out.println("Loading line " + lineNum);

				String[] parts = line == null ? null : line.split(" ");
				if (line == null || parts[0].equals("usemtl") || parts[0].equals("o")) {

					if (curMtl != null) {

						WFObject data = new WFObject();
						data.vertices = new float[vertices.size()];
						data.uvs = new float[uvs.size()];
						data.indices = new int[indices.size()];
						for (int i = 0; i < vertices.size(); i++) {
							data.vertices[i] = vertices.get(i);
						}
						for (int i = 0; i < uvs.size(); i++) {
							data.uvs[i] = uvs.get(i);
						}
						for (int i = 0; i < indices.size(); i++) {
							data.indices[i] = indices.get(i);
						}

						// generate random color
						data.idColor = new float[] { (float) Math.random(), (float) Math.random(),
								(float) Math.random() };
						
						data.mtl = curMtl.clone();
						objects.add(data);

						indices.clear();

					}
					if (line != null) {
						if (parts[0].equals("o")) {

							curObjName = parts[1];

							curMtl = null;
							vertices.clear();
							uvs.clear();
							indices.clear();

							rawVertOffset += rawVertices.size();
							rawUVOffset += rawUVs.size();

							rawVertices.clear();
							rawUVs.clear();

						} else {
							curMtl = materials.get(line.split(" ", 2)[1]);
						
						}
					}

				} else if (parts[0].equals("v")) {
					rawVertices.add(Float.parseFloat(parts[1]));
					rawVertices.add(Float.parseFloat(parts[2]));
					rawVertices.add(Float.parseFloat(parts[3]));
				} else if (parts[0].equals("vt")) {
					rawUVs.add(Float.parseFloat(parts[1]));
					rawUVs.add(Float.parseFloat(parts[2]));

				} else if (parts[0].equals("f")) {

					List<Integer> untriangulatedIndices = new ArrayList<Integer>();

					for (int i = 1; i < parts.length; i++) {
						String[] face = parts[i].split("/");
						int vertexIndex = Integer.parseInt(face[0]) - 1;
						vertices.add(rawVertices.get(vertexIndex * 3 - rawVertOffset));
						vertices.add(rawVertices.get(vertexIndex * 3 + 1 - rawVertOffset));
						vertices.add(rawVertices.get(vertexIndex * 3 + 2 - rawVertOffset));

						if (face.length > 1 && face[1].length() > 0) {
							int uvIndex = Integer.parseInt(face[1]) - 1;
							uvs.add(rawUVs.get(uvIndex * 2 - rawUVOffset));

							uvs.add(rawUVs.get(uvIndex * 2 + 1 - rawUVOffset));
						} else {
							uvs.add(0f);
							uvs.add(0f);
						}
						untriangulatedIndices.add(vertices.size() / 3 - 1);
					}

					List<Integer> triangulatedIndices = triangulatePolygon(vertices, uvs, untriangulatedIndices);

					indices.addAll(triangulatedIndices);

				} else if (parts[0].equals("mtllib")) {

					String file1 = line.split(" ", 2)[1];
					String fpath = path + file1;
					importMaterialFile(fpath,path, materials);

				}

			} while (line != null);
			System.out.println("Loaded " + objects.size() + " objects");
			
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}

		} catch (Exception e) {
			System.out.println("Error on line " + lineNum + " \t" + line);
			throw new RuntimeException(e);
		}

		SceneNode node = new SceneNode(root, false);

		for (WFObject obj : objects) {
			new Renderable(node, obj);
		}
		return node;

	}

}
