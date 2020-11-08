package com.nikosar.animeforever.discord.command.processor

import org.modelmapper.MappingException
import org.modelmapper.ModelMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

@Component
class SequentialParams(private val modelMapper: ModelMapper) {
    private val logger: Logger = LoggerFactory.getLogger(SequentialParams::class.java)

    fun convert(args: String, funcParams: List<KParameter>): Map<KParameter, Any> {
        return if (args.isNotEmpty()) {
            return try {
                args.split(" ").mapIndexed { i: Int, arg: String ->
                    val funcParam = funcParams[i + 2]
                    val paramClass = (funcParam.type.classifier!! as KClass<*>).java
                    val convertedArg = modelMapper.map<Any>(arg, paramClass)
                    Pair(funcParam, convertedArg)
                }.associate { it }
            } catch (e: MappingException) {
                logger.info("mapping failed for `$args`. Message: ${e.message}")
                emptyMap()
            }
        } else {
            emptyMap()
        }
    }
}