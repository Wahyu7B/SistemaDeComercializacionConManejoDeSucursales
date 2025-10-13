
const express = require('express');
const nodemailer = require('nodemailer');

const app = express();
app.use(express.json());

const PORT = 4000;

let transporter = nodemailer.createTransport({
    service: 'gmail', 
    auth: {
        user: 'segundollengle157@gmail.com',       
        pass: 'sfxgvavaipxdpgij'            
    },
});


app.post('/api/enviar-confirmacion', async (req, res) => {
    console.log("-> Petición recibida para enviar email de confirmación...");

    const { emailCliente, numeroPedido } = req.body;

    if (!emailCliente || !numeroPedido) {
        return res.status(400).json({ message: "Faltan datos requeridos." });
    }

    try {
        let info = await transporter.sendMail({
            from: '"Libros como Alas" <segundollengle157@gmail.com>',
            to: emailCliente,
            subject: `✅ Confirmación de tu Pedido #${numeroPedido}`,
            html: `<h1>¡Gracias por tu compra!</h1><p>Tu pedido #${numeroPedido} ha sido confirmado y está siendo preparado.</p>`,
        });

        console.log("Correo enviado exitosamente a:", emailCliente);
        
        res.status(200).json({ 
            message: "Correo de confirmación enviado exitosamente."
        });

    } catch (error) {
        console.error("Error al enviar el correo:", error);
        res.status(500).json({ message: "Error interno al enviar el correo." });
    }
});

app.post('/api/enviar-rechazo', async (req, res) => {
    console.log("-> Petición recibida para enviar email de RECHAZO...");

    const { emailCliente, numeroPedido } = req.body;

    if (!emailCliente || !numeroPedido) {
        return res.status(400).json({ message: "Faltan datos requeridos." });
    }

    try {
        await transporter.sendMail({
            from: '"Libros como Alas" <tu-correo@gmail.com>',
            to: emailCliente,
            subject: `❌ Novedades sobre tu Pedido #${numeroPedido}`,
            html: `
                <h1>Lo sentimos</h1>
                <p>Hubo un problema al procesar el pago de tu pedido #${numeroPedido} y ha sido anulado.</p>
                <p>El stock de los productos ha sido restaurado. Si crees que esto es un error, por favor, contáctanos.</p>
            `,
        });

        console.log("Correo de rechazo enviado exitosamente a:", emailCliente);
        res.status(200).json({ message: "Correo de rechazo enviado." });

    } catch (error) {
        console.error("Error al enviar el correo de rechazo:", error);
        res.status(500).json({ message: "Error interno del servidor." });
    }
});

app.listen(PORT, () => {
    console.log(`✅ Servicio de notificaciones (Gmail) corriendo en http://localhost:${PORT}`);
});