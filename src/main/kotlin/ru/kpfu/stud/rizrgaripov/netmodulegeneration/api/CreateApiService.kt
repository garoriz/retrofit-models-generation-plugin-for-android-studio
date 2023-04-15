package ru.kpfu.stud.rizrgaripov.netmodulegeneration.api

import com.intellij.openapi.components.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Service
@State(name = "CreateApiData", storages = [Storage("CreateApiData.xml")])
class CreateApiService : PersistentStateComponent<CreateApiService.State> {
    companion object {
        val instance: CreateApiService
            get() = ServiceManager.getService(CreateApiService::class.java)
    }

    var createApiState = State()

    override fun getState(): State {
        return createApiState
    }

    override fun loadState(stateLoadedFromPersistence: State) {
        createApiState = stateLoadedFromPersistence
    }

    class State {
        var httpMethod: HttpMethod by object : LoggingProperty<State, HttpMethod>(HttpMethod.GET) {}
        var url: String by object : LoggingProperty<State, String>("") {}
        var funcName: String by object : LoggingProperty<State, String>("") {}
        var returnValDataType: String by object : LoggingProperty<State, String>("") {}
        var asyncSupport: AsyncSupport by object : LoggingProperty<State, AsyncSupport>(AsyncSupport.SUSPEND) {}
        var isFormEncoded: Boolean by object : LoggingProperty<State, Boolean>(false) {}
        var isMultipart: Boolean by object : LoggingProperty<State, Boolean>(false) {}
        var isStreaming: Boolean by object : LoggingProperty<State, Boolean>(false) {}

        open class LoggingProperty<R, T>(initValue: T) : ReadWriteProperty<R, T> {
            private var backingField: T = initValue

            override operator fun getValue(thisRef: R, property: KProperty<*>): T {
                return backingField
            }

            override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
                backingField = value
            }
        }
    }

    enum class HttpMethod {
        GET,
        POST,
        PUT,
        DELETE,
        PATCH,
        HEAD,
        OPTIONS,
        HTTP
    }

    enum class AsyncSupport(val string: String) {
        SUSPEND("suspend"),
        SINGLE("io.reactivex.rxjava3.core.Single"),
        COMPLETABLE("io.reactivex.rxjava3.core.Completable")
    }
}