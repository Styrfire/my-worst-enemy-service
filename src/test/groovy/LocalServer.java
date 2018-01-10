import com.myWorstEnemy.Server;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class LocalServer extends Server
{
	public static void main(String[] args)
	{
		new LocalServer().configure(new SpringApplicationBuilder()).run(args);
	}
}
