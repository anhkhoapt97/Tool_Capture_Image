
package captureimage;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

        
public class CaptureImage {
    public CaptureImage() throws IOException {
        this.writer = new FileOutputStream("ForensicHardDisk.dat");
        this.tree = new FileOutputStream("dirTree.dat");
    }
    public static ArrayList<String> path = new ArrayList<>();
    FileOutputStream writer;
    FileOutputStream tree;
    String newline = System.getProperty("line.separator");
    String flag = "|___|";
    byte [] data2 = flag.getBytes();
    int i = 0;
    public void fetchChild(File file) throws FileNotFoundException, IOException
    {
        int i;
        if(file.isDirectory())// kiểm tra thư mục
        {
            
             String thumuc = file.getAbsolutePath();
             String[] dir = thumuc.split("\\:");
            byte[] thumucname=dir[1].getBytes();
            writer.write(thumucname,0,thumucname.length);
            writer.write(data2);
            File[] children = file.listFiles();// nếu là thư mục tạo ra ds các file 
            if (children == null) return;
            for (File child : children) // duyệt ds vừa tạo
            {
                if(child.isFile()) //kiểm tra là file hay thự mục
                {
                    FileInputStream fi = new FileInputStream(child.getAbsolutePath());
                    String thumucon = child.getAbsolutePath();
                    String[] son = thumucon.split("\\:");
                    byte[] filename = son[1].getBytes();
                    writer.write(filename, 0, filename.length);
                    do {
                        i = fi.read();
                        if (i != -1) {
                            writer.write(i);
                        }
                    } while (i != -1);
                    writer.write(data2, 0, data2.length);
                    fi.close();
                }
            }
            for (File child : children) // backtracking
            {
                this.fetchChild(child);
            }
        }
    }
    private void readUsingBufferedReader(String fileName)
        throws IOException {
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
            // process the line
            System.out.println(line);
            path.add(line);
        }
        br.close();
        fr.close();
    }
     private void listChild(File file, int level) throws IOException {
         if (file.getName().compareToIgnoreCase("System Volume Information") == 0) return;
        if (file.isDirectory()) { // Dừng nếu là tập tin
            String thumuc = getPadding(level) + " - " + file.getName() + newline;
            byte[] thumucname=thumuc.getBytes();
            tree.write(thumucname,0,thumucname.length);
            File[] children = file.listFiles();
            for (File child : children) {
                this.listChild(child, level + 1); // Gọi đệ quy
            }
        } else {
            String thumucon = getPadding(level) + " + " + file.getName() + newline;
            byte[] filename = thumucon.getBytes();
            tree.write(filename, 0, filename.length);
        }
 
    }
    private int check(File file, int level) throws IOException {
        // if (file.getName().compareToIgnoreCase("System Volume Information") == 0) return 1;
        if (file.isDirectory()) { // Dừng nếu là tập tin
            String thumuc = getPadding(level) + " - " + file.getName() ;
           if (thumuc.compareTo(path.get(i)) != 0 ) return 1;
            File[] children = file.listFiles();
            for (File child : children) {
                this.check(child, level + 1); // Gọi đệ quy
            }
        } else {
            String thumucon = getPadding(level) + " + " + file.getName() ;
            if (thumucon.compareToIgnoreCase(path.get(i)) != 0 ) return 1;
            
        }
        i++;
        return 2;
    }
    private String getPadding(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= level; i++) {
            sb.append("    "); // Thêm dấu tab.
        }
        return sb.toString();
    }
    public static void main(String[] args) throws IOException {
        CaptureImage Forensic = new CaptureImage();
        boolean flag = true;
        int chon;
        String check, kiemtra;
        while (flag)
        {
            path.clear();
            Scanner sc = new Scanner(System.in); 
            System.out.println("Menu:\n1. Capture tất cả ổ đĩa.\n2. Nhập đường dẫn thư mục cần capture .\n3. Thoát.");
            System.out.print("Nhập lựa chọn: ");
            chon = sc.nextInt();
            sc.nextLine();
            if (chon!=1 && chon !=2) break;
            switch(chon)
            {
                case 1:
                    
                    File[] roots = File.listRoots();// tạo ds tất cả ổ đĩa
                    for (File root : roots)
                    {
                        File child = new File(root.getAbsolutePath());
                        Forensic.listChild(child,0);
                        Forensic.readUsingBufferedReader("dirTree.dat");
                        int kt = Forensic.check(child, 0);
                        if (kt == 2) System.out.println("Cây thư mục bạn duyệt chính xác!!!");
                        Forensic.fetchChild(child);
                    }
                    break;
                case 2:    
                    
                    boolean a;
                    File f;
                    String yesorno = null;
                    do
                    {
                        System.out.print("Nhập đường dẫn thư mục: ");
                        check = sc.nextLine();
                        f = new File(check);
                        a = f.exists();
                        if(!a) 
                        {
                            System.out.println("Thư mục không tồn tại");
                            System.out.println("Bạn có muốn nhập lại không?(y)");
                            yesorno = sc.nextLine();
                            
                        }
                        if ("y".equalsIgnoreCase(yesorno)) continue; 
                        else break;
                    } while(!a);
                    Forensic.listChild(f,0);
                    Forensic.readUsingBufferedReader("dirTree.dat");
                    int kt = Forensic.check(f, 0);
                    if (kt == 2) System.out.println("Cây thư mục bạn duyệt chính xác!!!");
                    Forensic.fetchChild(f);
                    break;          
            }
        }
    }
}
