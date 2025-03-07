/*import 'another.w'
list := [3, 5, 4]
println(list.get(1))

func test() -> {
    a := 5
    func inner(b) -> {
        println(a * b)
   }
   return inner
}
example := test()
example(4)
*/
/*
test := {
    'example': 1,
    true: false
}

println(test.get('example'))
println(test.get(true))
*/
import 'std.io'

type Bird(name, speed) -> {
    func fly() -> {
        io.println('🕊️ Bird: ' + self.name + ' is flying with speed: ' + speed)
    }
}

unit Nest -> {
    birds := []
    func add_bird(bird) -> {
        self.birds.add(bird)
    }
    func print_birds() -> {
        io.println('🎖️ Birds list: ')
        for i in 0 to birds.size() {
            io.println(
                birds.get(i).name
                + ' :: ' + birds.get(i).speed
            )
        }
    }
}

bird := new Bird('Sofy', 4.67)
Nest.add_bird(bird)
Nest.print_birds()