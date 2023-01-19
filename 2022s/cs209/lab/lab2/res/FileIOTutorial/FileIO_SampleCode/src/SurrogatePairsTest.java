
public class SurrogatePairsTest {

	public static void main(String[] args) {
		String s=String.valueOf(Character.toChars(0x10437));  		
		System.out.println(s); 
		System.out.println(s.charAt(0)); 
		
		char[]chars=s.toCharArray(); 
		for(char c:chars){  
		    System.out.format("%x",(short)c);  
		}  
	}
}

