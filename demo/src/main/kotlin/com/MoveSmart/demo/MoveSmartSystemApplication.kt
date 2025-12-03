package com.movesmart.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MoveSmartApplication {
	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			runApplication<MoveSmartApplication>(*args)
		}
	}
}


