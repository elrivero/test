package cl.a2r.micampo.app.custom;

public class BastonObject {
	
	private String name;
	private String macAdress;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getMacAdress() {
		return macAdress;
	}
	
	public void setMacAdress(String macAdress) {
		this.macAdress = macAdress;
	}
	
	public String toString(){
		return name + "\n" + macAdress;
	}
}
