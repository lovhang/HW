
public class exa {

	public static  void main (String[] arg) {
		for(int i=1;i<10;i++) {
			int shu = 1;
			for(int j=1;j<=9-i;j++) {
				System.out.print("     ");
			}
			for(int j=1;j<=i;j++) {				
					shu=shu*2;
					System.out.print(shu+"    ");
				
			}
			for(int j=1;j<i;j++) {
			
				shu=shu/2;
				System.out.print(shu+"    ");
			}
			System.out.println();
		}
	}
}
