import com.myWorstEnemy.Server;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class LocalServer extends Server
{
	public static void main(String[] args)
	{
		System.setProperty("environment", "localhost");
		new LocalServer().configure(new SpringApplicationBuilder()).run(args);
	}
}
