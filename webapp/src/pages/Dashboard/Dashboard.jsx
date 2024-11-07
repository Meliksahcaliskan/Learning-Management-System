import { useContext } from "react";
import { AuthContext } from '../../contexts/AuthContext';




const Dashboard = () => {

    const { user } = useContext(AuthContext);


    if(!user) return <p>Lorem ipsum dolor sit amet consectetur, adipisicing elit. Suscipit rerum modi dolorem earum possimus accusamus iure aliquam iusto ipsam ipsum. Obcaecati, dignissimos. Repellat impedit ratione distinctio sequi! Quasi beatae omnis labore delectus placeat iure fuga, libero quibusdam in non consequuntur aspernatur a debitis culpa provident! Perferendis, ut provident sunt repellat obcaecati voluptatem hic expedita quae blanditiis velit quaerat aperiam qui ex in, inventore rerum ullam magnam facilis tempora minus culpa assumenda? Totam aut excepturi deleniti recusandae repudiandae. Quae eligendi ullam deserunt quo libero ipsum ab earum officia? Similique hic recusandae eum porro fugit tempore sed commodi doloribus nobis. Asperiores, magni officiis, atque iure harum soluta quis est repudiandae nam pariatur minus nostrum ullam, adipisci dolores! Eos dignissimos nobis quia accusantium praesentium molestias id iure sapiente ex, magnam quae. Dicta consequuntur hic facere quaerat veritatis vel modi velit. Deleniti cumque, fugit eum amet ullam eius reiciendis quasi vel molestiae maxime veniam. Est, aspernatur qui tempore sed perferendis necessitatibus, accusamus beatae consequuntur exercitationem ipsum dicta facere rem quas earum harum dolorem. Fuga deleniti natus, blanditiis architecto exercitationem et voluptatum qui autem, dicta culpa ipsum cum nihil quaerat beatae doloremque alias fugiat eum. Ullam cum sed ipsa at eos dolorem sapiente iure pariatur?</p>

    return(
        <div>
            <h1>welcome</h1>
            <p>username : {user.username}</p>
            <p>email : {user.email}</p>
            <p>role : {user.role}</p>
        </div>
    );

}
export default Dashboard