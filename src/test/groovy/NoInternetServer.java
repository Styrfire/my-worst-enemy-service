import com.myWorstEnemy.Server;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class NoInternetServer extends Server
{
	public static void main(String[] args)
	{
		System.setProperty("spring.profiles.active", "nointernet");
		new NoInternetServer().configure(new SpringApplicationBuilder()).run(args);
	}
}
