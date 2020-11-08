package com.nikosar.animeforever.discord.command.processor

import org.modelmapper.MappingException
import org.modelmapper.ModelMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSubclassOf

@Component
class SequentialParams(private val modelMapper: ModelMapper) {
    private val logger: Logger = LoggerFactory.getLogger(SequentialParams::class.java)

    fun convert(args: String, funcParams: List<KParameter>): Map<KParameter, Any> {
        return if (args.isNotEmpty()) {
            return try {
                args.split(" ")
                        .mapIndexed(createParamValuePair(funcParams))
                        .associate { it }
            } catch (e: MappingException) {
                logger.info("mapping failed for `$args`. Message: ${e.message}")
                emptyMap()
            }
        } else {
            emptyMap()
        }
    }

    private fun createParamValuePair(funcParams: List<KParameter>): (Int, String) -> Pair<KParameter, Any> {
        return { i: Int, arg: String ->
            val funcParam = funcParams[i + 2]
            val paramClass = (funcParam.type.classifier!! as KClass<*>)
            val value = arg.let { if (paramClass.isSubclassOf(Enum::class)) arg.toUpperCase() else arg }
            val convertedArg = modelMapper.map<Any>(value, paramClass.java)
            Pair(funcParam, convertedArg)
        }
    }
}