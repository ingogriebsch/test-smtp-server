package de.ingogriebsch.utils.testsmtpserver;

import static java.lang.String.format;
import static java.lang.System.nanoTime;
import static java.net.InetAddress.getByName;

import java.util.Random;
import java.util.function.Supplier;

import javax.net.ServerSocketFactory;

final class RandomPortSupplier implements Supplier<Integer> {
	private static final Random random = new Random(nanoTime());
	private static final Integer MIN_PORT = 1024;
	private static final Integer MAX_PORT = 65535;
	private static final Integer PORT_RANGE = MAX_PORT - MIN_PORT;

	@Override
	public Integer get() {
		Integer counter = 0;

		Integer candidate;
		do {
			if (counter > PORT_RANGE) {
				throw new IllegalStateException(
						format("Could not find an available port in the range [%d, %d] after %d attempts", MIN_PORT,
								MAX_PORT, counter));
			}
			candidate = random.nextInt(PORT_RANGE + 1);
			counter++;
		} while (!isAvailable(candidate));
		return candidate;
	}

	private static boolean isAvailable(Integer port) {
		try {
			ServerSocketFactory.getDefault().createServerSocket(port, 1, getByName("localhost")).close();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}
