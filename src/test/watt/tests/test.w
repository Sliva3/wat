import 'std.io'

func example(call) -> {
    if call == false {
        return null
    }

    hello := 'hello world'
    io.println('before: ' + hello)
    example(false)
    io.println('after: ' + hello)
}

example(true)