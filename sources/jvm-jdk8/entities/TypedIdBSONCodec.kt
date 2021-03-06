package io.fluidsonic.server

import org.bson.*


internal class TypedIdBSONCodec(
	private val idFactoryProvider: EntityIdFactoryProvider
) : AbstractBSONCodec<TypedId, BSONCodingContext>() {

	override fun BsonReader.decode(context: BSONCodingContext) =
		readDocument {
			val factory = readString("type").let { type ->
				idFactoryProvider.idFactoryForType(type) ?: throw BSONException("ID type '$type' has not been registered with Baku")
			}

			readName("id")
			readValueOfType(factory.idClass).typed
		}


	override fun BsonWriter.encode(value: TypedId, context: BSONCodingContext) {
		writeDocument {
			write("type", string = value.untyped.factory.type)
			write("id", value = value.untyped)
		}
	}
}
