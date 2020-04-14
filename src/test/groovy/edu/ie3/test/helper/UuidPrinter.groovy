/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.helper

/**
 * Simple class to print a bunch of uuids for later static usage in code
 */
class UuidPrinter {
	static void main(String[] args) {
		new File('./','src/test/resources/uuids.txt').withWriter('utf-8') { writer ->
			for ( int cnt = 0; cnt < 199 ; cnt++ ) {
				writer.writeLine "UUID.fromString(\"" + UUID.randomUUID() + "\"),"
			}
		}
	}
}
